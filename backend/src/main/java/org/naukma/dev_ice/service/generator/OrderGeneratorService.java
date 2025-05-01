package org.naukma.dev_ice.service.generator;

import org.naukma.dev_ice.entity.*;
import org.naukma.dev_ice.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@Service
public class OrderGeneratorService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private final Random random = new Random();

    public void generateOrders(int count) {
        List<Manager> managers = managerRepository.findAll();
        List<Product> products = productRepository.findAll();
        List<Customer> customers = customerRepository.findAll();

        for (int i = 0; i < count; i++) {
            Order order = new Order();
            order.setManager(randomFrom(managers));
            order.setCustomer(randomFrom(customers));
            order.setStatus(randomStatus());
            order.setPlacementDate(randomTimestamp());
            order.setDispatchDate(random.nextBoolean() ? Timestamp.from(Instant.now()) : null);
            order.setPaymentMethod(randomPaymentMethod());
            order.setPayed(random.nextBoolean());
            order.setPost(randomPost());
            order.setPostOffice("№" + (random.nextInt(100) + 1));

            order.setOrderAmount(0.0);

            order = orderRepository.save(order);
            Set<OrderProduct> orderProducts = generateOrderProducts(order, products);

            double totalAmount = 0.0;
            for (OrderProduct op : orderProducts) {
                orderProductRepository.save(op);
                totalAmount += op.getProduct().getSellingPrice() * op.getNumber();
            }

            order.setOrderAmount(totalAmount);
            orderRepository.save(order);
        }
    }

    private Set<OrderProduct> generateOrderProducts(Order order, List<Product> products) {
        Set<OrderProduct> orderProducts = new HashSet<>();
        int productCount = random.nextInt(5) + 1;

        List<Product> shuffled = new ArrayList<>(products);
        Collections.shuffle(shuffled);

        for (int i = 0; i < productCount; i++) {
            Product product = shuffled.get(i);
            int quantity = random.nextInt(5) + 1;

            OrderProductId id = new OrderProductId(order.getOrderId(), product.getProductId());
            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setId(id);
            orderProduct.setOrder(order);
            orderProduct.setProduct(product);
            orderProduct.setNumber(quantity);

            orderProducts.add(orderProduct);
        }

        return orderProducts;
    }

    private <T> T randomFrom(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }

    private String randomStatus() {
        return List.of("NEW", "IN PROCESS", "DELIVERED", "CANCELLED").get(random.nextInt(4));
    }

    private String randomPaymentMethod() {
        return List.of("CARD", "CASH", "PAYPAL").get(random.nextInt(3));
    }

    private String randomPost() {
        return List.of("Nova Poshta", "Ukrposhta", "Meest").get(random.nextInt(3));
    }

    private Timestamp randomTimestamp() {
        long now = System.currentTimeMillis();
        long past = now - (long) (random.nextInt(60 * 60 * 24 * 30) * 1000L);
        return new Timestamp(past);
    }
}
