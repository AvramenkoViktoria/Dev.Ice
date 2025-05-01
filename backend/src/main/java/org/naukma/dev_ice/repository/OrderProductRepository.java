package org.naukma.dev_ice.repository;

import org.naukma.dev_ice.entity.OrderProduct;
import org.naukma.dev_ice.entity.OrderProductId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductRepository extends JpaRepository<OrderProduct, OrderProductId> {
}
