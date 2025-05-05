package org.naukma.dev_ice.service.generator;

import org.naukma.dev_ice.entity.Product;
import org.naukma.dev_ice.entity.Sale;
import org.naukma.dev_ice.repository.ProductRepository;
import org.naukma.dev_ice.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class ProductGeneratorService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SaleRepository saleRepository;

    private final Random random = new Random();

    public void generateSalesAndProducts(int salesNum, int productsNum) {
        generateRandomSales(salesNum);
        generateRandomProducts(productsNum);
    }

    public void generateRandomProducts(int number) {
        for (int i = 0; i < number; i++) {
            Product product = new Product();
            product.setName("Product " + (i + 1));
            product.setPurchasePrice(generateRandomPrice(5000, 500000));
            product.setSellingPrice(product.getPurchasePrice() * 2);
            product.setCategory(generateRandomCategory());

            product.setInStock(random.nextBoolean());
            if (product.getInStock().equals(false))
                product.setStorageQuantity(0);
            else
                product.setStorageQuantity(random.nextInt(100) + 1);

            product.setProducer(generateRandomProducer());
            product.setBrand(product.getProducer());

            int count = saleRepository.countAll();
            if (count > 0) {
                long randomId = 1 + new Random().nextInt(count);
                Sale randomSale = saleRepository.findById(randomId);
                product.setSaleId(randomSale.getSaleId());
            }

            productRepository.save(product);
        }
    }

    public void generateRandomSales(int number) {
        for (int i = 0; i < number; i++) {
            Sale sale = new Sale();
            sale.setName("Sale " + (i + 1));
            sale.setDiscountValue(random.nextInt(1, 51));
            saleRepository.save(sale);
        }
    }

    private double generateRandomPrice(int min, int max) {
        return min + (max - min) * random.nextDouble();
    }

    private String generateRandomCategory() {
        String[] categories = {"Office", "Gaming", "Design", "Ultralight", "Education"};
        return categories[random.nextInt(categories.length)];
    }

    private String generateRandomProducer() {
        LaptopManufacturerService service = new LaptopManufacturerService();
        return service.getRandomManufacturer();
    }

    private int generateRandomRam() {
        int[] ramOptions = {4, 8, 16, 32, 64};
        return ramOptions[random.nextInt(ramOptions.length)];
    }

    private String generateRandomColor() {
        String[] colors = {"Black", "White", "Red", "Blue", "Green", "Pink", "Yellow", "Gold", "Purple", "Cyan"};
        return colors[random.nextInt(colors.length)];
    }

    private String generateRandomCountry() {
        String[] countries = {"USA", "China", "Germany", "Japan", "India"};
        return countries[random.nextInt(countries.length)];
    }

    private java.sql.Date generateRandomYear() {
        int year = random.nextInt(11) + 2015;
        return java.sql.Date.valueOf(year + "-01-01");
    }

    private double generateRandomDiagonal() {
        return 11 + (random.nextDouble() * 7);
    }

    private int generateRandomInternalStorage() {
        int[] storageOptions = {128, 256, 512, 1000};
        return storageOptions[random.nextInt(storageOptions.length)];
    }

}
