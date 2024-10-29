package com.example.cookit.controllers;

import com.example.cookit.services.AppUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/user")
public class AppUserController {

    @Autowired
    private AppUserService appUserService;


    @PostMapping(value = "/add-photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> addUserPhoto (@RequestPart("id") String uuid,
                                        @RequestPart("file") MultipartFile file) {
        return appUserService.addOrUpdatePhoto(uuid, file);
    }
}
