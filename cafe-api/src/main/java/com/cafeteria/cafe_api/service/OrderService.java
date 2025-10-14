package com.cafeteria.cafe_api.service;

import com.cafeteria.cafe_api.model.Order;
import com.cafeteria.cafe_api.model.Product;
import com.cafeteria.cafe_api.repository.OrderRepository;
import com.cafeteria.cafe_api.repository.ProductRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final MeterRegistry meterRegistry;

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        MeterRegistry meterRegistry) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.meterRegistry = meterRegistry;
    }

    // 🔹 Crear una nueva orden
    public Order createOrder(Order order) {
        Product product = productRepository.findById(order.getProduct().getId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        order.setProduct(product);
        order.setStatus(Order.Status.NEW);
        order.setCreatedAt(LocalDateTime.now());

        Order saved = orderRepository.save(order);

        // Métrica: cantidad total de órdenes creadas
        meterRegistry.counter("coffee_orders_created_total").increment();

        return saved;
    }

    // 🔹 Obtener todas las órdenes
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // 🔹 Obtener una orden por ID
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
    }

    // 🔹 Actualizar el estado de una orden
    public Order updateOrderStatus(Long id, String status) {
        Order order = getOrderById(id);

        try {
            Order.Status newStatus = Order.Status.valueOf(status.toUpperCase());
            order.setStatus(newStatus);

            // Métrica: cantidad total de órdenes entregadas
            if (newStatus == Order.Status.DELIVERED) {
                meterRegistry.counter("coffee_orders_delivered_total").increment();
            }

            return orderRepository.save(order);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Estado inválido: " + status);
        }
    }

    // 🔹 Eliminar una orden
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("Orden no encontrada");
        }
        orderRepository.deleteById(id);
    }
}

