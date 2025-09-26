package com.cafeteria.cafe_api.repository;

import com.cafeteria.cafe_api.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
