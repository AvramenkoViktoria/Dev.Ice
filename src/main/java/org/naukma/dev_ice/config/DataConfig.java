package org.naukma.dev_ice.config;

import org.naukma.dev_ice.service.CustomerGeneratorService;
import org.naukma.dev_ice.service.ProductGeneratorService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataConfig {

    private final ProductGeneratorService productGenerator;
    private final CustomerGeneratorService customerGenerator;

    public DataConfig(ProductGeneratorService productGenerator, CustomerGeneratorService customerGenerator) {
        this.productGenerator = productGenerator;
        this.customerGenerator = customerGenerator;
    }

    @Bean
    public CommandLineRunner dataInitializer() {
        return args -> {
            productGenerator.generateSalesAndProducts(10, 500);
            customerGenerator.generateCustomers(300);
        };
    }
}
