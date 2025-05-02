package org.naukma.dev_ice.controller;

import org.json.JSONObject;
import org.naukma.dev_ice.dto.OrderRequestDto;
import org.naukma.dev_ice.dto.OrderUpdateRequestDto;
import org.naukma.dev_ice.entity.Order;
import org.naukma.dev_ice.repository.OrderCustomRepository;
import org.naukma.dev_ice.repository.OrderRepository;
import org.naukma.dev_ice.service.SaveOrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderCustomRepository orderRepository;
    private final SaveOrderService saveOrderService;
    private final OrderRepository plainOrderRepository;

    public OrderController(OrderCustomRepository orderRepository, SaveOrderService saveOrderService, OrderRepository plainOrderRepository) {
        this.orderRepository = orderRepository;
        this.saveOrderService = saveOrderService;
        this.plainOrderRepository = plainOrderRepository;
    }

    @PostMapping("/search")
    public List<Map<String, Object>> searchOrders(@RequestBody String json) {
        JSONObject queryParams = new JSONObject(json);
        return orderRepository.findOrders(queryParams);
    }

    @PostMapping("/add")
    public ResponseEntity<Order> addOrder(@RequestBody OrderRequestDto dto) {
        Order savedOrder = saveOrderService.createOrder(dto);
        return ResponseEntity.ok(savedOrder);
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateOrder(@RequestBody OrderUpdateRequestDto request) {
        try {
            plainOrderRepository.update(request.getOrder(), request.getOrderProducts());
            return ResponseEntity.ok("Order updated successfully");
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update order: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable("id") Long id) {
        try {
            plainOrderRepository.deleteById(id);
            return ResponseEntity.ok("Order deleted successfully");
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete order: " + e.getMessage());
        }
    }
}
