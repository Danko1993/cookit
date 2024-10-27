package com.example.cookit.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class MealSchedule {
    @Id
    private UUID id=UUID.randomUUID();



    private UUID dietPlanId;

    @Temporal(TemporalType.DATE)
    private Date date;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "meal_schedule_meals",
            joinColumns = @JoinColumn(name = "meal_schedule_id"),
            inverseJoinColumns = @JoinColumn(name = "meal_id")
    )
    @JsonManagedReference
    private List<Meal> meals;

    private double calories;

    private double carbs;

    private double proteins;

    private double fats;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MealSchedule{")
                .append("id=").append(id)
                .append(", date=").append(date)
                .append(", meals=[");
        for (int i = 0; i < meals.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(meals.get(i).getName()); // Assuming getName() gives a simple representation
        }
        sb.append("]}");
        return sb.toString();
    }
}
