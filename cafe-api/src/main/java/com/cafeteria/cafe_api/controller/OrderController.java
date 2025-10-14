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

    // 🔹 Crear una nueva orden (cliente, producto y cantidad)
    @PostMapping
    public Order create(@RequestBody Order order) {
        return service.createOrder(order);
    }

    // 🔹 Obtener todas las órdenes
    @GetMapping
    public List<Order> all() {
        return service.getAllOrders();
    }

    // 🔹 Obtener una orden específica por ID
    @GetMapping("/{id}")
    public Order getById(@PathVariable Long id) {
        return service.getOrderById(id);
    }

    // 🔹 Actualizar el estado de una orden (por ejemplo: READY, DELIVERED, CANCELLED)
    @PatchMapping("/{id}/status/{status}")
    public Order updateStatus(@PathVariable Long id, @PathVariable String status) {
        return service.updateOrderStatus(id, status);
    }

    // 🔹 Eliminar una orden
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteOrder(id);
    }
}

