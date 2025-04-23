package org.naukma.dev_ice.repository;

import org.naukma.dev_ice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
