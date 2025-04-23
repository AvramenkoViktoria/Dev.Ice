package org.naukma.dev_ice.service;

import com.github.javafaker.Faker;
import org.naukma.dev_ice.entity.Customer;
import org.naukma.dev_ice.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

@Service
public class CustomerGeneratorService {

    private static final Faker faker = new Faker(new Locale("en"));
    private static final Set<String> usedEmails = new HashSet<>();
    private static final Random random = new Random();

    @Autowired
    private CustomerRepository customerRepository;

    public Customer generateUniqueCustomer() {
        String email;
        do {
            email = faker.name().username().replaceAll("[^a-zA-Z0-9]", "").toLowerCase() + "@gmail.com";
        } while (!usedEmails.add(email));

        String phone = generatePhoneNumber();

        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setPhoneNum(phone);
        customer.setFirstName(faker.name().firstName());
        customer.setSecondName(faker.name().lastName());
        customer.setLastName(faker.name().lastName());
        customer.setPassword(faker.internet().password(8, 16));

        return customer;
    }

    private String generatePhoneNumber() {
        StringBuilder sb = new StringBuilder("+380");
        for (int i = 0; i < 9; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    public void generateCustomers(int count) {
        for (int i = 0; i < count; i++)
            customerRepository.save(generateUniqueCustomer());
    }
}
