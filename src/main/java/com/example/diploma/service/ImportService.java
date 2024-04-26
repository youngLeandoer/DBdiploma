package com.example.diploma.service;

import org.springframework.stereotype.Service;

@Service
public class ImportService {

    private final DatabaseService databaseService;

    public ImportService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }
}
