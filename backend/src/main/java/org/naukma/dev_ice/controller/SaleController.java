package org.naukma.dev_ice.controller;

import org.naukma.dev_ice.entity.Sale;
import org.naukma.dev_ice.repository.SaleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    private final SaleRepository saleRepository;

    public SaleController(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    @PostMapping
    public ResponseEntity<Void> addSale(@RequestBody Sale sale) {
        saleRepository.save(sale);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Sale>> getAllSales() {
        return ResponseEntity.ok(saleRepository.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateSale(@PathVariable Long id, @RequestBody Sale sale) {
        try {
            saleRepository.update(id, sale);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSale(@PathVariable Long id) {
        try {
            saleRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

