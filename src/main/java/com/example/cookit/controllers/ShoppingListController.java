package com.example.cookit.controllers;

import com.example.cookit.DTO.ReadyShopingListDto;
import com.example.cookit.DTO.ShoppingListDto;
import com.example.cookit.services.ShoppingListService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/shopping_list")
public class ShoppingListController {
    @Autowired
    private ShoppingListService shoppingListService;

    @PostMapping("/add")
    public ResponseEntity<String> addShoppingListAndBindToUser(@RequestBody @Valid ShoppingListDto shoppingListDto, BindingResult bindingResult) {
        log.info("Preparing shopping list : {} to save in database", shoppingListDto.name());
        if (bindingResult.hasErrors() && bindingResult!=null){
            log.warn("Validation errors : {}", bindingResult);
            return new ResponseEntity<>("Validation errors : "+bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        return shoppingListService.addShoppingListAndBindWithUser(shoppingListDto);
    }

    @GetMapping("/get_by_user")
    public ResponseEntity<List<ReadyShopingListDto>> getShoppingListByUser(@RequestParam("id") UUID id) {
        return shoppingListService.getReadyShoppingListsByUser(id);
    }

}
