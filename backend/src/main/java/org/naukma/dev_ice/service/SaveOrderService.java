package org.naukma.dev_ice.service;

import org.naukma.dev_ice.dto.OrderRequestDto;
import org.naukma.dev_ice.entity.Customer;
import org.naukma.dev_ice.entity.Order;
import org.naukma.dev_ice.entity.OrderProduct;
import org.naukma.dev_ice.entity.Product;
import org.naukma.dev_ice.repository.CustomerRepository;
import org.naukma.dev_ice.repository.OrderProductRepository;
import org.naukma.dev_ice.repository.OrderRepository;
import org.naukma.dev_ice.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class SaveOrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderProductRepository orderProductRepository;

    public SaveOrderService(OrderRepository orderRepository,
                            CustomerRepository customerRepository,
                            ProductRepository productRepository,
                            OrderProductRepository orderProductRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.orderProductRepository = orderProductRepository;
    }

    public Order createOrder(OrderRequestDto dto) {
        Customer customer;
        try {
            customer = customerRepository.findById(dto.customerEmail);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch customer", e);
        }

        if (customer == null) {
            throw new IllegalArgumentException("Customer not found.");
        }

        Order order = new Order();
        order.setCustomerEmail(customer.getEmail());
        order.setStatus(dto.status);
        order.setPlacementDate(dto.placementDate);
        order.setDispatchDate(dto.dispatchDate);
        order.setPaymentMethod(dto.paymentMethod);
        order.setPayed(dto.payed);
        order.setPost(dto.post);
        order.setPostOffice(dto.postOffice);
        order.setOrderAmount(dto.orderAmount);

        try {
            Order savedOrder = orderRepository.save(order);

            for (OrderRequestDto.ProductQuantity pq : dto.products) {
                Product product;
                try {
                    product = productRepository.findById(pq.productId);
                } catch (SQLException e) {
                    throw new RuntimeException("Failed to fetch product: " + pq.productId, e);
                }

                if (product == null)
                    throw new IllegalArgumentException("Product not found: " + pq.productId);

                OrderProduct orderProduct = new OrderProduct();
                orderProduct.setOrderId(savedOrder.getOrderId());
                orderProduct.setProductId(product.getProductId());
                orderProduct.setNumber(pq.number);

                orderProductRepository.save(orderProduct);
            }

            return savedOrder;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save order or related products", e);
        }
    }
}

