package org.naukma.dev_ice.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class LaptopManufacturerService {

    private final List<String> manufacturers = List.of(
            "Samsung", "Apple", "Sony", "LG", "Huawei", "Dell", "HP", "Lenovo", "Acer", "Asus",
            "Microsoft", "Razer", "Toshiba", "MSI", "Alienware", "Xiaomi", "Google", "Vaio",
            "Chuwi", "Gigabyte", "Dynabook", "Panasonic", "Medion", "Fujitsu", "Vivo", "Sharp",
            "Compal", "Foxconn", "Zotac", "Kaspersky", "Pipo", "Meizu", "OnePlus", "Bluboo",
            "Wacom", "GPD", "Mecer", "Teclast", "Pavilion", "Clevo", "Bose", "Epson", "Tangent",
            "Schenker", "Hyrican", "Haier", "Acer Predator", "Aorus", "Evesham", "Packard Bell", "Colorful"
    );

    public String getRandomManufacturer() {
        Random random = new Random();
        int index = random.nextInt(manufacturers.size());
        return manufacturers.get(index);
    }
}
