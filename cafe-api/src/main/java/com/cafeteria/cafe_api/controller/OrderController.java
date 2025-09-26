package com.cafeteria.cafe_api.controller;

import com.cafeteria.cafe_api.model.Order;
import com.cafeteria.cafe_api.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping("/{productId}")
    public Order create(@PathVariable Long productId) {
        return service.create(productId);
    }

    @PostMapping("/{orderId}/pay")
    public Order pay(@PathVariable Long orderId) {
        return service.pay(orderId);
    }

    @PostMapping("/{orderId}/cancel")
    public Order cancel(@PathVariable Long orderId) {
        return service.cancel(orderId);
    }

    @GetMapping
    public List<Order> all() {
        return service.all();
    }
}
