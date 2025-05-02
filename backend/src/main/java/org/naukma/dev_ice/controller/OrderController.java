package org.naukma.dev_ice.controller;

import org.json.JSONObject;
import org.naukma.dev_ice.dto.OrderRequestDto;
import org.naukma.dev_ice.entity.Order;
import org.naukma.dev_ice.repository.OrderCustomRepository;
import org.naukma.dev_ice.service.SaveOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderCustomRepository orderRepository;
    private final SaveOrderService saveOrderService;

    public OrderController(OrderCustomRepository orderRepository, SaveOrderService saveOrderService) {
        this.orderRepository = orderRepository;
        this.saveOrderService = saveOrderService;
    }

    @PostMapping("/search")
    public List<Map<String, Object>> searchOrders(@RequestBody String json) {
        JSONObject queryParams = new JSONObject(json);
        return orderRepository.findOrders(queryParams);
    }

    @PostMapping("/save")
    public ResponseEntity<Order> saveOrder(@RequestBody OrderRequestDto dto) {
        Order savedOrder = saveOrderService.createOrder(dto);
        return ResponseEntity.ok(savedOrder);
    }
}
