package ru.zakrzhevskiy.lighthouse.service;

import com.lowagie.text.DocumentException;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.groovy.util.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;
import ru.zakrzhevskiy.lighthouse.model.Order;
import ru.zakrzhevskiy.lighthouse.model.User;
import ru.zakrzhevskiy.lighthouse.model.enums.AfterOrderProcessed;
import ru.zakrzhevskiy.lighthouse.repository.OrderRepository;
import ru.zakrzhevskiy.lighthouse.repository.UserRepository;
import ru.zakrzhevskiy.lighthouse.utils.HTMLPrintUtil;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class OrderFormService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${temp.catalog.path:/tmp/order_forms_generation}")
    private String tmpPath;

    @Autowired
    @Qualifier("orderFormTemplateEngine")
    private TemplateEngine templateEngine;

    public byte[] generateOrderForm(String htmlFilePath, String outputFilePath) {
        return HTMLPrintUtil.htmlFileToPDF(htmlFilePath, outputFilePath);
    }

    @SneakyThrows
    public Path generatePdf(Order order, User user) {
        Resource staticResource = new ClassPathResource("static");

        String html = parseThymeleafTemplate(order, user);

        Path rootDir = Paths.get(tmpPath);

        Path tmpDir = Files.createTempDirectory(rootDir, "");
        Path outputFile = Paths.get(tmpDir.toFile().getAbsolutePath(), String.format("order_%s_form.pdf", order.getId()));

        FileUtils.copyDirectory(staticResource.getFile().getAbsoluteFile(), tmpDir.toFile().getAbsoluteFile());

        Path tmpHTML = Files.createTempFile(tmpDir, "order_", ".html");

        FileUtils.writeStringToFile(tmpHTML.toFile(), html, UTF_8);

        HTMLPrintUtil.generatePdfAsProcess(tmpHTML.toFile().getAbsolutePath(), outputFile.toFile().getAbsolutePath());

        return outputFile;
    }

    @SneakyThrows
    public String parseThymeleafTemplate(Order order, User owner) {
        Context context = new Context();

        context.setVariable("orderNumber", order.getOrderNumber());
        context.setVariable("userFio", owner.getMyUserDetails().getFIO());
        context.setVariable("userPhone", owner.getMyUserDetails().getPhoneNumber());
        context.setVariable("userEmail", owner.getEMail());
        context.setVariable("userInstagram", String.join(", ", owner.getMyUserDetails().getInstagram()));

        context.setVariable(
                "afterOrderProcessed",
                order.getAfterOrderProcessed() != null
                        ? order.getAfterOrderProcessed().name()
                        : AfterOrderProcessed.SAVE
        );

        if (order.getAfterOrderProcessed().equals(AfterOrderProcessed.SEND_BY_MAIL)) {
            String companyCode = order.getTransportCompany().getCode();

            context.setVariable("transportCompanyCode", companyCode);

            List<String> addressComponents = Arrays.asList(
                    owner.getMyUserDetails().getPostalCode(),
                    owner.getMyUserDetails().getCountry(),
                    owner.getMyUserDetails().getCity(),
                    owner.getMyUserDetails().getAddress()
            );

            String addressLine = String.join(", ", addressComponents);
            context.setVariable("address", addressLine);
        }

        context.setVariable("scanner", order.getScanner());
        context.setVariable("orderType", order.getOrderType());
        context.setVariable("scanSize", order.getScanSize());

        context.setVariable("colorTones", order.getColorTones());
        context.setVariable("contrast", order.getContrast());
        context.setVariable("density", order.getDensity());
        context.setVariable("frame", order.getFrame());
        context.setVariable("package", order.getPack());
        context.setVariable("express", order.getExpress());

        List<String> headers = Arrays.asList("FilmType", "ProcessType", "Count", "ScanSize", "Push");
        List<Map<String, Object>> rows = new ArrayList<>();

        order.getOrderFilms().forEach(film -> {
            Map<String, Object> map = Maps.of("FilmType", film.getFilmType(),
                    "ProcessType", film.getProcessingType(),
                    "Count", film.getQuantity(),
                    "ScanSize", film.getResolution(),
                    "Push", film.getPush()
            );

            rows.add(map);
        });

        context.setVariable("films", rows);
        context.setVariable("special", order.getSpecial());
        context.setVariable("nextStatusURL", String.format("%s/orders/order/%s/nextStatus", "http://localhost:8080", order.getId()));
        context.setVariable("host", "");

        return templateEngine.process("thymeleaf_template", context);
    }

//    public Path generatePdfFromHtml(String html) {
//        Path temp = createTempFile();
//        OutputStream outputStream = createOutputStream(Objects.requireNonNull(temp));
//
//        ITextRenderer renderer = new ITextRenderer();
//        renderer.setDocumentFromString(html);
//        renderer.layout();
//        createPDF(renderer, outputStream);
//
//        closeOS(Objects.requireNonNull(outputStream));
//
//        return temp;
//    }

    public Path createTempFile(Path baseDir) {
        try {
            return Files.createTempFile(baseDir, "order", ".pdf");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private OutputStream createOutputStream(Path temp) {
        try {
            return new FileOutputStream(temp.toFile());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void createPDF(ITextRenderer renderer, OutputStream outputStream) {
        try {
            renderer.createPDF(outputStream);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    private void closeOS(OutputStream outputStream) {
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
