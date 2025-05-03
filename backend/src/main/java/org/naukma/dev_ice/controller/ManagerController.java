package org.naukma.dev_ice.controller;

import org.naukma.dev_ice.dto.ProductSalesDto;
import org.naukma.dev_ice.entity.Manager;
import org.naukma.dev_ice.repository.ManagerRepository;
import org.naukma.dev_ice.service.ManagerProductStatisticsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/managers")
public class ManagerController {

    private final ManagerRepository managerRepository;
    private final ManagerProductStatisticsService managerProductStatisticsService;

    public ManagerController(ManagerRepository managerRepository, ManagerProductStatisticsService managerProductStatisticsService) {
        this.managerRepository = managerRepository;
        this.managerProductStatisticsService = managerProductStatisticsService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addManager(@RequestBody Manager manager) {
        try {
            try {
                manager.setStartDate(Timestamp.from(Instant.parse(manager.getStartDate().toString())));
                if (manager.getFinishDate() != null)
                    manager.setFinishDate(Timestamp.from(Instant.parse(manager.getFinishDate().toString())));
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.badRequest().body("Invalid date format");
            }
            managerRepository.save(manager);
            return ResponseEntity.ok("Manager saved");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to save manager: " + e.getMessage());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateManager(@RequestBody Manager manager) {
        try {
            try {
                manager.setStartDate(Timestamp.from(Instant.parse(manager.getStartDate().toString())));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Invalid startDate format");
            }
            if (manager.getFinishDate() != null && !manager.getFinishDate().toString().isEmpty()) {
                try {
                    manager.setFinishDate(Timestamp.from(Instant.parse(manager.getFinishDate().toString())));
                } catch (Exception e) {
                    manager.setFinishDate(null);
                }
            }

            managerRepository.update(manager);
            return ResponseEntity.ok("Manager updated successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body("Failed to update manager: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteManager(@PathVariable("id") Long id) {
        try {
            managerRepository.deleteById(id);
            return ResponseEntity.ok("Manager deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete manager: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Manager>> getAllManagers() {
        return ResponseEntity.ok(managerRepository.findAll());
    }

    @PostMapping("/sales-statistics")
    public ResponseEntity<List<ProductSalesDto>> getManagerProductSales(@RequestBody String jsonInput) {
        try {
            List<ProductSalesDto> productSales = managerProductStatisticsService.getManagerProductSalesFromJson(jsonInput);
            return ResponseEntity.ok(productSales);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }
}
