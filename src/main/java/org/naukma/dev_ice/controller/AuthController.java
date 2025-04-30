package org.naukma.dev_ice.controller;

import lombok.RequiredArgsConstructor;
import org.naukma.dev_ice.entity.Customer;
import org.naukma.dev_ice.repository.CustomerRepository;
import org.naukma.dev_ice.repository.ManagerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final CustomerRepository customerRepository;
    private final ManagerRepository managerRepository;
    ;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public String register(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String rawPassword = request.get("password");

        boolean isManager = managerRepository.existsByEmail(username);
        boolean isCustomer = customerRepository.existsByEmail(username);

        if (isManager)
            return "Email is already registered as a manager!";
        if (isCustomer)
            return "Email is already registered as a customer!";

        Customer customer = new Customer();
        customer.setEmail(username);
        customer.setPassword(passwordEncoder.encode(rawPassword));
        customerRepository.save(customer);

        return "Registration successful!";
    }

    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String rawPassword = request.get("password");

        return customerRepository.findById(email)
                .filter(c -> passwordEncoder.matches(rawPassword, c.getPassword()))
                .map(c -> "Login successful!")
                .orElse("Invalid credentials.");
    }
}
