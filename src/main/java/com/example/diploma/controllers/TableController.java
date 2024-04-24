package com.example.diploma.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Controller
public class TableController {

    @PostMapping("/upload")
    public ResponseEntity<String> uploadData(@RequestParam(required = false) String tableData,
                                             @RequestParam(required = false) MultipartFile excelFile) {
        Map<String, Object> responseData = new HashMap<>();
        if (tableData != null || (excelFile != null && !excelFile.isEmpty())) {
            // Обработка данных и возврат ответа
            responseData.put("success", true);
            responseData.put("message", "Данные успешно загружены");
        } else {
            responseData.put("success", false);
            responseData.put("message", "Пожалуйста, введите данные таблицы или выберите файл Excel.");
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(responseData);
            return ResponseEntity.ok(jsonResponse);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при обработке данных");
        }
    }

    @GetMapping("/table")
    public String getTablePage() {
        return "index";
    }
}