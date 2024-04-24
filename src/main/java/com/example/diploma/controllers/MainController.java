package com.example.diploma.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/index")
    public String mainPage() {
     return "index";
    }

    @GetMapping("/select_database")
    public String selectDatabasePage() {
        return "select_database";
    }

    @GetMapping("/map_colums")
    public String selectMappingPage(){
        return "map_colums";
    }
}
