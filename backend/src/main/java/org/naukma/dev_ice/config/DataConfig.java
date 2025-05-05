package org.naukma.dev_ice.config;

import org.naukma.dev_ice.service.generator.CustomerGeneratorService;
import org.naukma.dev_ice.service.generator.ManagerGeneratorService;
import org.naukma.dev_ice.service.generator.OrderGeneratorService;
import org.naukma.dev_ice.service.generator.ProductGeneratorService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataConfig {

    private final CustomerGeneratorService customerGenerator;
    private final ManagerGeneratorService managerGenerator;
    private final ProductGeneratorService productGenerator;
    private final OrderGeneratorService orderGenerator;
    private final DataBaseInitializer dataBaseInitializer;

    public DataConfig(DataBaseInitializer dataBaseInitializer,
                      CustomerGeneratorService customerGenerator,
                      ManagerGeneratorService managerGenerator,
                      ProductGeneratorService productGenerator,
                      OrderGeneratorService orderGenerator) {
        this.dataBaseInitializer = dataBaseInitializer;
        this.customerGenerator = customerGenerator;
        this.managerGenerator = managerGenerator;
        this.productGenerator = productGenerator;
        this.orderGenerator = orderGenerator;
    }

    @Bean
    public CommandLineRunner dataInitializer() {
        return args -> {
            dataBaseInitializer.createTables();

            managerGenerator.generateManagers(3);
            customerGenerator.generateCustomers(10);
            productGenerator.generateSalesAndProducts(5, 50);
            orderGenerator.generateOrders(10);
        };
    }
}
