package com.cafeteria.cafe_api.repository;

import com.cafeteria.cafe_api.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
