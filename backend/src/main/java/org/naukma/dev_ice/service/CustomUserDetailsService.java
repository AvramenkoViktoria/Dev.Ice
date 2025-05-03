package org.naukma.dev_ice.service;

import org.naukma.dev_ice.entity.Customer;
import org.naukma.dev_ice.entity.Manager;
import org.naukma.dev_ice.repository.CustomerRepository;
import org.naukma.dev_ice.repository.ManagerRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final CustomerRepository customerRepository;
    private final ManagerRepository managerRepository;

    public CustomUserDetailsService(CustomerRepository customerRepository, ManagerRepository managerRepository) {
        this.customerRepository = customerRepository;
        this.managerRepository = managerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Trying to authenticate user: " + username);

        Manager manager = managerRepository.findByEmail(username);
        if (manager != null) {
            System.out.println("Manager found: " + manager.getEmail());
            System.out.println("Manager password: " + manager.getPassword());
            return new User(manager.getEmail(), manager.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_MANAGER")));
        }

        Customer customer = customerRepository.findByEmail(username);
        if (customer != null) {
            System.out.println("Customer found: " + customer.getEmail());
            return new User(customer.getEmail(), customer.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER")));
        }

        throw new UsernameNotFoundException("User not found with username: " + username);
    }

}
