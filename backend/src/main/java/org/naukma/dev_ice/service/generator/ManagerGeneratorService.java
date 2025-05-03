package org.naukma.dev_ice.service.generator;

import com.github.javafaker.Faker;
import org.naukma.dev_ice.entity.Manager;
import org.naukma.dev_ice.repository.ManagerRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Random;

@Service
public class ManagerGeneratorService {

    private final ManagerRepository managerRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final Faker faker = new Faker(new Locale("en"));
    private static final Random random = new Random();

    private static final String OUTPUT_FILE = Paths.get("src", "main", "resources", "manager_data.txt").toString();

    public ManagerGeneratorService(ManagerRepository managerRepository) {
        this.managerRepository = managerRepository;
    }

    public void generateManagers(int count) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE, false))) {
            for (int i = 0; i < count; i++) {
                Manager manager = new Manager();

                manager.setFirstName(faker.name().firstName());
                manager.setSecondName(faker.name().lastName());
                manager.setLastName(faker.name().lastName());

                LocalDate start = LocalDate.now().minusDays(random.nextInt(1825));
                manager.setStartDate(Timestamp.valueOf(start.atStartOfDay()));

                if (random.nextDouble() < 0.05) {
                    LocalDate finish = start.plusDays(random.nextInt(365));
                    if (finish.isBefore(LocalDate.now())) {
                        manager.setFinishDate(Timestamp.valueOf(finish.atStartOfDay()));
                    }
                }

                manager.setPhoneNum(generatePhoneNumber());
                manager.setEmail(faker.internet().emailAddress());

                String rawPassword = faker.internet().password(8, 16);
                manager.setPassword(rawPassword);
                managerRepository.save(manager);

                saveManagerToFile(writer, manager, rawPassword);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to write manager data to file", e);
        }
    }

    private void saveManagerToFile(BufferedWriter writer, Manager manager, String rawPassword) throws IOException {
        writer.write(String.format(
                "Name: %s %s, Email: %s, Password: %s%n",
                manager.getFirstName(),
                manager.getLastName(),
                manager.getEmail(),
                rawPassword
        ));
    }

    private String generatePhoneNumber() {
        StringBuilder sb = new StringBuilder("+380");
        for (int i = 0; i < 9; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
