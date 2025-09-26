package com.cafeteria.cafe_api.service;

import com.cafeteria.cafe_api.model.Order;
import com.cafeteria.cafe_api.model.Product;
import com.cafeteria.cafe_api.repository.OrderRepository;
import com.cafeteria.cafe_api.repository.ProductRepository;
import io.micrometer.core.instrument.Metrics;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orders;
    private final ProductRepository products;

    public OrderService(OrderRepository orders, ProductRepository products) {
        this.orders = orders;
        this.products = products;
    }

    // Crear nueva orden para un producto
    public Order create(Long productId) {
        Product product = products.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        Order order = new Order();
        order.setProduct(product);
        order.setStatus(Order.Status.CREATED);

        Order saved = orders.save(order);

        // Métrica Prometheus
        Metrics.counter("orders_total",
                "product", String.valueOf(productId),
                "status", "created").increment();

        return saved;
    }

    // Pagar una orden
    public Order pay(Long orderId) {
        Order order = orders.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada"));

        order.setStatus(Order.Status.PAID);
        Order saved = orders.save(order);

        Metrics.counter("orders_total",
                "product", String.valueOf(order.getProduct().getId()),
                "status", "paid").increment();

        return saved;
    }

    // Cancelar una orden
    public Order cancel(Long orderId) {
        Order order = orders.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada"));

        order.setStatus(Order.Status.CANCELLED);
        Order saved = orders.save(order);

        Metrics.counter("orders_total",
                "product", String.valueOf(order.getProduct().getId()),
                "status", "cancelled").increment();

        return saved;
    }

    // Listar todas las órdenes
    public List<Order> all() {
        return orders.findAll();
    }
}
