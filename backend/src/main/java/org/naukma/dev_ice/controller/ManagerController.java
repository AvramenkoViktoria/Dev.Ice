package org.naukma.dev_ice.controller;

import org.naukma.dev_ice.entity.Manager;
import org.naukma.dev_ice.repository.ManagerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/managers")
public class ManagerController {

    private final ManagerRepository managerRepository;

    public ManagerController(ManagerRepository managerRepository) {
        this.managerRepository = managerRepository;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addManager(@RequestBody Manager manager) {
        try {
            managerRepository.save(manager);
            return ResponseEntity.ok("Manager added successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add manager: " + e.getMessage());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateManager(@RequestBody Manager manager) {
        try {
            managerRepository.update(manager);
            return ResponseEntity.ok("Manager updated successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update manager: " + e.getMessage());
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
}
