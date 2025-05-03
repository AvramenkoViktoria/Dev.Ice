package org.naukma.dev_ice.service.generator;

import com.github.javafaker.Faker;
import org.naukma.dev_ice.entity.Customer;
import org.naukma.dev_ice.repository.CustomerRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

@Service
public class CustomerGeneratorService {

    private static final Faker faker = new Faker(new Locale("en"));
    private static final Set<String> usedEmails = new HashSet<>();
    private static final Random random = new Random();
    private static final String FILE_PATH = Paths.get("src", "main", "resources", "customer_data.txt").toString();

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerGeneratorService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

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

        String rawPassword = faker.internet().password(8, 16);
        customer.setPassword(passwordEncoder.encode(rawPassword));

        saveCustomerToFile(email, customer.getFirstName(), rawPassword);

        return customer;
    }

    private void saveCustomerToFile(String email, String name, String rawPassword) {
        try {
            Path filePath = Paths.get("src", "main", "resources", "customer_data.txt");
            Files.createDirectories(filePath.getParent());

            try (FileWriter writer = new FileWriter(filePath.toFile(), false)) {
                writer.write(String.format("Email: %s, Name: %s, Password: %s%n", email, name, rawPassword));
            }
        } catch (IOException e) {
            System.err.println("Failed to write customer data to file: " + e.getMessage());
        }
    }

    private String generatePhoneNumber() {
        StringBuilder sb = new StringBuilder("+380");
        for (int i = 0; i < 9; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    public void generateCustomers(int count) {
        for (int i = 0; i < count; i++) {
            customerRepository.save(generateUniqueCustomer());
        }
    }
}
