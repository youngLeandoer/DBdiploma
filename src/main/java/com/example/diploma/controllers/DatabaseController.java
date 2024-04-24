package com.example.diploma.controllers;

// DatabaseController.java

import com.example.diploma.service.DatabaseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/database")
@CrossOrigin(origins = "http://localhost:8080")
public class DatabaseController {

    private final DatabaseService databaseService;

    public DatabaseController(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @GetMapping("/list")
    public ResponseEntity<?> getDatabaseList() {
        List<String> databaseList = databaseService.getDatabaseList();
        return ResponseEntity.ok(databaseList);
    }

    @PostMapping("/connect")
    public ResponseEntity<?> connectToDatabase(@RequestParam String database) {
        boolean connected = databaseService.connectToDatabase(database);
        if (connected) {
            return ResponseEntity.ok("Connected to database: " + database);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to connect to database: " + database);
        }
    }

    @GetMapping("/imported-data")
    public ResponseEntity<?> getImportedData(@RequestParam String database) {
        List<Map<String, Object>> importedData = databaseService.getImportedData(database);
        return ResponseEntity.ok(importedData);
    }
}
