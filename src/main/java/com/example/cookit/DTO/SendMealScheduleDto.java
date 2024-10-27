package com.example.cookit.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendMealScheduleDto {
    private UUID id;
    private Date date;
    private List<SendMealDto> meals;
    private double calories;

    private double carbs;

    private double proteins;

    private double fats;
}
