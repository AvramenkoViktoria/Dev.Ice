package org.naukma.dev_ice.controller;

import org.json.JSONObject;
import org.naukma.dev_ice.repository.OrderCustomRepository;
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

    public OrderController(OrderCustomRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @PostMapping("/search")
    public List<Map<String, Object>> searchOrders(@RequestBody String json) {
        JSONObject queryParams = new JSONObject(json);
        return orderRepository.findOrders(queryParams);
    }
}
