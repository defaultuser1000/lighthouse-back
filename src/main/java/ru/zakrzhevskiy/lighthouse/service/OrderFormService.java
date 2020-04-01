package ru.zakrzhevskiy.lighthouse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.zakrzhevskiy.lighthouse.model.Order;
import ru.zakrzhevskiy.lighthouse.repository.OrderRepository;
import ru.zakrzhevskiy.lighthouse.utils.HTMLPrintUtil;

@Service
public class OrderFormService {

    @Autowired
    private OrderRepository orderRepository;

    public byte[] generateOrderForm(Long id) {
        Order order = orderRepository.findOrderById(id);
        byte[] pdf = HTMLPrintUtil.printPdfReport("OrderForm_ru.html", order);

//        try {
//            OutputStream os = new FileOutputStream(new File("target/OrderForm_ru.pdf"));
//            os.write(pdf);
//            os.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return pdf;
    }
}
