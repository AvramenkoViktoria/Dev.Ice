package org.naukma.dev_ice.repository;

import org.naukma.dev_ice.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
    boolean existsByEmail(String email);
    boolean existsByPhoneNum(String phoneNum);
    Customer findByEmail(String email);
}
