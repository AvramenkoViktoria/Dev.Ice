package org.naukma.dev_ice.service;

import org.naukma.dev_ice.dto.OrderRequestDto;
import org.naukma.dev_ice.entity.Customer;
import org.naukma.dev_ice.entity.Order;
import org.naukma.dev_ice.repository.CustomerRepository;
import org.naukma.dev_ice.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class SaveOrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;

    public SaveOrderService(OrderRepository orderRepository, CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
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
        order.setCustomer(customer);
        order.setStatus(dto.status);
        order.setPlacementDate(dto.placementDate);
        order.setDispatchDate(dto.dispatchDate);
        order.setPaymentMethod(dto.paymentMethod);
        order.setPayed(dto.payed);
        order.setPost(dto.post);
        order.setPostOffice(dto.postOffice);
        order.setOrderAmount(dto.orderAmount);

        try {
            return orderRepository.save(order);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save order", e);
        }
    }
}
