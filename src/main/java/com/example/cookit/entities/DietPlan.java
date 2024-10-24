package com.example.cookit.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class DietPlan {
    @Id
    private UUID id = UUID.randomUUID();

    private String name;

    @ManyToOne
    @JoinColumn(name = "app_user_id", nullable = false)
    @JsonBackReference
    private AppUser appUser;

    private Double dailyCalories;

//    @OneToMany(mappedBy = "dietPlan", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//    @JsonManagedReference
//    private List<MealSchedule> mealSchedules;
}
