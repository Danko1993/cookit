package com.example.cookit.controllers;

import com.example.cookit.DTO.HomeDto;
import com.example.cookit.services.HomeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@RestController
public class HomeController {
    @Autowired
    private HomeService homeService;

    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    @GetMapping("/home")
    public ResponseEntity<HomeDto> getHomeData() {
        return homeService.getHomeData();
    }

}
