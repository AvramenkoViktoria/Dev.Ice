package org.naukma.dev_ice.service;

import com.github.javafaker.Faker;
import org.naukma.dev_ice.entity.Manager;
import org.naukma.dev_ice.repository.ManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;

@Service
public class ManagerGeneratorService {

    private static final Faker faker = new Faker(new Locale("en"));
    private static final Random random = new Random();

    @Autowired
    private ManagerRepository managerRepository;

    public void generateManagers(int count) {
        for (int i = 0; i < count; i++) {
            Manager manager = new Manager();

            manager.setFirstName(faker.name().firstName());
            manager.setSecondName(faker.name().lastName());
            manager.setLastName(faker.name().lastName());

            LocalDate start = LocalDate.now().minusDays(random.nextInt(1825));
            manager.setStartDate(Timestamp.valueOf(start.atStartOfDay()));

            if (random.nextDouble() < 0.05) {
                LocalDate finish = start.plusDays(random.nextInt(365));
                if (finish.isBefore(LocalDate.now()))
                    manager.setFinishDate(Timestamp.valueOf(finish.atStartOfDay()));
            }

            manager.setPhoneNum(generatePhoneNumber());
            managerRepository.save(manager);
        }
    }

    private String generatePhoneNumber() {
        StringBuilder sb = new StringBuilder("+380");
        for (int i = 0; i < 9; i++)
            sb.append(random.nextInt(10));
        return sb.toString();
    }
}
