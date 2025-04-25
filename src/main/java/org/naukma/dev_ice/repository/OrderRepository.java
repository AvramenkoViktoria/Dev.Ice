package org.naukma.dev_ice.repository;

import org.naukma.dev_ice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
