package org.naukma.dev_ice.config;

import org.naukma.dev_ice.service.generator.CustomerGeneratorService;
import org.naukma.dev_ice.service.generator.ProductGeneratorService;
import org.naukma.dev_ice.service.generator.ManagerGeneratorService;
import org.naukma.dev_ice.service.generator.OrderGeneratorService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataConfig {

    private final ProductGeneratorService productGenerator;
    private final CustomerGeneratorService customerGenerator;
    private final ManagerGeneratorService managerGenerator;
    private final OrderGeneratorService orderGenerator;

    public DataConfig(ManagerGeneratorService managerGenerator, ProductGeneratorService productGenerator, CustomerGeneratorService customerGenerator, OrderGeneratorService orderGenerator) {
        this.managerGenerator = managerGenerator;
        this.productGenerator = productGenerator;
        this.customerGenerator = customerGenerator;
        this.orderGenerator = orderGenerator;
    }

    @Bean
    public CommandLineRunner dataInitializer() {
        return args -> {
//            managerGenerator.generateManagers(5);
//            productGenerator.generateSalesAndProducts(10, 500);
//            customerGenerator.generateCustomers(300);
//            orderGenerator.generateOrders(500);
        };
    }
}
