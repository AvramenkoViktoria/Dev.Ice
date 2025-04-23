package org.naukma.dev_ice.config;

import org.naukma.dev_ice.service.ProductGeneratorService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataConfig {

    private final ProductGeneratorService productGenerator;

    public DataConfig(ProductGeneratorService productGenerator) {
        this.productGenerator = productGenerator;
    }

    @Bean
    public CommandLineRunner dataInitializer() {
        return args -> {
            productGenerator.generateSalesAndProducts();
        };
    }
}
