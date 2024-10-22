package com.example.cookit.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadyShopingListDto {

    private String name;

    private Map<String, Double> ingredientsWithWeight;
}
