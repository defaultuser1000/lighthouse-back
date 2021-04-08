package ru.zakrzhevskiy.lighthouse.controller;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.SneakyThrows;
import org.apache.tika.io.IOUtils;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.zakrzhevskiy.lighthouse.model.*;
import ru.zakrzhevskiy.lighthouse.model.dto.StorageItemDto;
import ru.zakrzhevskiy.lighthouse.model.price.OrderType;
import ru.zakrzhevskiy.lighthouse.model.price.ScanSize;
import ru.zakrzhevskiy.lighthouse.model.price.Scanner;
import ru.zakrzhevskiy.lighthouse.model.views.View;
import ru.zakrzhevskiy.lighthouse.repository.*;
import ru.zakrzhevskiy.lighthouse.service.OrderFormService;
import ru.zakrzhevskiy.lighthouse.service.storage.StorageService;

import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderFormService orderFormService;
    private final Logger log = LoggerFactory.getLogger(OrderController.class);
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private FilmRepository filmRepository;
    @Autowired
    private MessagesRepository messagesRepository;
    @Autowired
    private OrderStatusRepository orderStatusRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ScanSizeRepository scanSizeRepository;
    @Autowired
    private OrderTypeRepository orderTypeRepository;
    @Autowired
    private ScannerRepository scannerRepository;
    @Autowired
    private StorageService storageService;

    // Yandex.Disk base folder path
    private final String BASE_FOLDER = "";

    @SneakyThrows
    @RequestMapping(
            method = RequestMethod.GET,
            produces = APPLICATION_JSON_VALUE
    )
    @JsonView(View.OrderUser.class)
    @Transactional
    public ResponseEntity<?> orders(
            Principal principal,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize,
            @RequestParam(required = false, defaultValue = "DESC") Sort.Direction direction,
            @RequestParam(required = false, defaultValue = "modificationDate") String sortBy
    ) {

        String username = principal.getName();
        User user = userRepository.findUserByUsername(username).orElseThrow(NotFound::new);

        Page<Order> orders;
        if (user.getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN"))) {
            orders = orderRepository.findAll(PageRequest.of(page, pageSize, Sort.by(direction, sortBy)));
        } else {
            orders = orderRepository.findByOrderOwner(user, PageRequest.of(page, pageSize, Sort.by(direction, sortBy)));
        }

        return ResponseEntity.ok().body(orders);
    }

    @RequestMapping(
            path = "/order/{id}/messages",
            method = RequestMethod.GET,
            produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getOrderMessages(@PathVariable Long id,
                                              @RequestParam(required = false, defaultValue = "0") Integer page,
                                              @RequestParam(required = false, defaultValue = "30") Integer pageSize,
                                              @RequestParam(required = false, defaultValue = "ASC") Sort.Direction direction,
                                              @RequestParam(required = false, defaultValue = "creationDate") String sortBy
    ) {
        Optional<Page<Message>> result = messagesRepository.findByOrderId(id, PageRequest.of(page, pageSize, Sort.by(direction, sortBy)));

        return result.map(response -> ResponseEntity.ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(
            path = "/order/{id}/messages",
            method = RequestMethod.POST,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE
    )
    @Transactional
    public ResponseEntity<?> createMessage(@PathVariable Long id, @Valid @RequestBody Message message, Principal principal) throws URISyntaxException {
        User creator = userRepository.findUserByUsername(principal.getName()).get();
        Order order = orderRepository.findOrderById(id);

        message.setText(Base64.getEncoder().encodeToString(message.getText().getBytes(UTF_8)));

        message.setUserId(creator.getId());
        message.setOrderId(order.getId());

        Message result = messagesRepository.save(message);

        return ResponseEntity.created(new URI("/orders/order/" + order.getId() + "/messages/" + result.getId())).body(result);
    }

    @RequestMapping(
            path = "/order/messages/{messageId}",
            method = RequestMethod.DELETE
    )
    public ResponseEntity<?> deleteMessage(@PathVariable Long messageId) {
        Message message = messagesRepository.getOne(messageId);

        messagesRepository.delete(message);

        return ResponseEntity.ok().build();
    }

    @RequestMapping(
            path = "/statistics",
            method = RequestMethod.GET,
            produces = APPLICATION_JSON_VALUE
    )
    @Transactional
    public ResponseEntity<?> getOrdersStatistics(Principal principal) {
        String username = principal.getName();
        User user = userRepository.findUserByUsername(username).get();

        List<Order> orders = orderRepository.findByOrderOwner(user);

        Map<String, Integer> ordersStatistics = new LinkedHashMap<>();
        ordersStatistics.put("All", orders.size());
        orders.forEach(order -> {
            Integer value = ordersStatistics.containsKey(order.getOrderStatus().getDisplayName()) ? ordersStatistics.get(order.getOrderStatus().getDisplayName()) + 1 : 1;
            ordersStatistics.put(order.getOrderStatus().getDisplayName(), value);
        });

        return ResponseEntity.ok(ordersStatistics);
    }

    @RequestMapping(
            path = "/order/{id}",
            method = RequestMethod.GET,
            produces = APPLICATION_JSON_VALUE
    )
    @JsonView(View.OrderUser.class)
    public ResponseEntity<?> getOrder(@PathVariable Long id) {
        Optional<Order> order = orderRepository.findById(id);
        return order.map(response -> ResponseEntity.ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Transactional
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Order> createOrder(@Valid @RequestBody Order order, Principal principal) throws URISyntaxException {
        log.info("Request to create order: {}", order);

        Order lastOrder = orderRepository.findTopByOrderByIdDesc();
        int orderNumber;
        if (lastOrder != null) {
            orderNumber = lastOrder.getOrderNumber() + 1;
        } else {
            orderNumber = 1001;
        }

        User creatorAndOwner = userRepository.findUserByUsername(principal.getName()).get();

        order.setOrderNumber(orderNumber);
        order.setOrderCreator(creatorAndOwner);
        order.setOrderOwner(creatorAndOwner);

        OrderStatus newOrderStatus = orderStatusRepository.findOrderStatusByDisplayName("New");
        order.setOrderStatus(newOrderStatus);

        ScanSize scanSize = scanSizeRepository.findBySize(order.getScanSize().getSize());
        OrderType orderType = orderTypeRepository.findByName(order.getOrderType().getName());
        Scanner scanner = scannerRepository.findByName(order.getScanner().getName());

        order.setScanSize(scanSize);
        order.setOrderType(orderType);
        order.setScanner(scanner);

        Order result = orderRepository.save(order);

        return ResponseEntity.created(new URI("/orders/order/" + result.getId())).body(result);
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            path = "/order/{id}",
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> updateOrder(@PathVariable Long id, @Valid @RequestBody Order order) {
        Order baseOrder = orderRepository.findOrderById(id);
        log.info("Request to update order: {}", baseOrder);
        Order result = orderRepository.save(order);
        return ResponseEntity.ok().body(result);
    }

    @RequestMapping(
            method = RequestMethod.POST,
            path = "/order/{id}/nextStatus",
            produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> nextStatus(@PathVariable Long id) {
        Order order = orderRepository.findOrderById(id);
        OrderStatus currentStatus = order.getOrderStatus();

        OrderStatus nextStatus = orderStatusRepository.getOne(currentStatus.getNextStatusId());

        order.setOrderStatus(nextStatus);

        orderRepository.save(order);

        return ResponseEntity.ok().build();

    }

    @RequestMapping(
            method = RequestMethod.GET,
            path = "/order/{id}/films",
            produces = APPLICATION_JSON_VALUE
    )
    public Iterable<Film> getOrderFilms(@PathVariable Long id) {
        return orderRepository.findOrderById(id).getOrderFilms();
    }

    @SneakyThrows
    @Transactional
    @RequestMapping(
            path = "/order/{id}/generateReport",
            method = RequestMethod.GET
    )
    public ResponseEntity<Resource> generateOrderForm(@PathVariable Long id, Principal principal) {
        User authenticatedUser = userRepository.findUserByUsername(principal.getName()).orElseThrow(NotFound::new);
        Order order = orderRepository.findOrderById(id);

        User orderOwner = userRepository.findUserById(order.getOrderOwner().getId());
        if (authenticatedUser.getRoles().stream().noneMatch(role -> role.getName().equals("ADMIN"))) {
            if (!order.getOrderOwner().getId().equals(authenticatedUser.getId())) {
                throw new AccessDeniedException("You not allowed to process that order...");
            }
        }

        File outputFile = orderFormService.generatePdf(order, orderOwner).toFile().getAbsoluteFile();

        byte[] content = IOUtils.toByteArray(new FileInputStream(outputFile));

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + outputFile.getName())
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(content.length)
                .body(new InputStreamResource(new ByteArrayInputStream(content)));
    }

    @RequestMapping(path = "/order/{id}/setOrderDiskDestination", method = RequestMethod.PUT)
    public ResponseEntity<?> setOrderDiskDespination(@PathVariable Long id, @RequestParam(name = "path") String destinationPath) {
        Order order = orderRepository.findOrderById(id);

        if (destinationPath.matches(".*")) {
            // TODO: Апи Диска: проверять наличие папки, создавать при необходимости
            order.setOrderDiskDestination(destinationPath);
            return ResponseEntity.ok(orderRepository.save(order).getOrderDiskDestination());
        } else {
            return ResponseEntity.badRequest().body("Not valid disk folder path.");
        }
    }

    @RequestMapping(path = "/order/{id}/createFolder", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<?> createFolder(@PathVariable("id") Long id,
                                          @RequestParam(name = "path", required = false) String path) {
        Order order = orderRepository.findOrderById(id);

        storageService.createFolder(order, path);

        return new ResponseEntity<>(null, null, HttpStatus.CREATED);
    }

    @RequestMapping(path = "/order/{id}/uploadPhoto", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<?> uploadFile(@PathVariable("id") Long id,
                                        @RequestParam(name = "path", required = false) String path,
                                        @RequestParam("files") MultipartFile... files
    ) {
        Order order = orderRepository.findOrderById(id);

        List<StorageItemDto> fileDTO = storageService.uploadFile(order, path, files);

        return new ResponseEntity<>(fileDTO, null, HttpStatus.CREATED);
    }

    @RequestMapping(path = "/order/{id}/getPhotos", method = RequestMethod.GET)
    @Transactional
    public ResponseEntity<?> listOrderPhotos(@PathVariable("id") Long id,
                                             @RequestParam(name = "additionalPath", required = false) String additionalPath
    ) {
        Order order = orderRepository.findOrderById(id);

        List<StorageItemDto> items = storageService.listDirContent(order, additionalPath);

        return ResponseEntity.ok(items);
    }

    @RequestMapping(path = "/order/{id}/editPhotos", method = RequestMethod.PUT)
    @Transactional
    public ResponseEntity<?> editOrderPhotos(@PathVariable("id") Long id,
                                             @RequestParam(name = "path", required = false) String path,
                                             @RequestParam("files") MultipartFile... files
    ) {
        Order order = orderRepository.findOrderById(id);

//        storageService.

        return ResponseEntity.ok().build();
    }

    @SneakyThrows
    @Transactional
    @RequestMapping(
            path = "/order/{id}/downloadPhotos",
            method = RequestMethod.GET,
            produces = "application/zip"
    )
    public ResponseEntity<Object> downloadArchive(@PathVariable("id") Long id,
                                                  @RequestParam(name = "path", required = false) String path
    ) {
        Order order = orderRepository.findOrderById(id);

        String basePath = String.join(
                "/",
                "ready",
                order.getOrderOwner().getUsername(),
                order.getOrderNumber().toString()
        ) + (path != null ? "/" + path : "");

        return storageService.downloadFile(basePath + "/");
    }

    @RequestMapping(path = "/order/{id}/deletePhotos", method = RequestMethod.DELETE, consumes = APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<?> deleteFiles(@PathVariable("id") Long id, @Valid @RequestBody String... files) {
        Order order = orderRepository.findOrderById(id);

        storageService.deleteFiles(order, files);

        return ResponseEntity.ok().build();
    }

    @RequestMapping(
            path = "/getNewOrderFieldsValues",
            method = RequestMethod.GET,
            produces = APPLICATION_JSON_VALUE
    )
    @JsonView(View.Short.class)
    public ResponseEntity<?> getNewOrderFieldsValues() {
        Map<String, Object> result = new LinkedHashMap<>();
        List<String> scanners = scannerRepository.findAll().stream().map(Scanner::getName).collect(toList());
        List<String> scanTypes = orderTypeRepository.findAll().stream().map(OrderType::getName).collect(toList());
        List<ScanSize> scanSizes = scanSizeRepository.findAll();

        result.put("scanners", scanners);
        result.put("scanTypes", scanTypes);
        result.put("scanResolution", scanSizes);

        return ResponseEntity.ok(result);
    }
}
