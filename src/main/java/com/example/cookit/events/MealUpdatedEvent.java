package com.example.cookit.events;

import com.example.cookit.entities.Meal;
import org.springframework.context.ApplicationEvent;

public class MealUpdatedEvent extends ApplicationEvent {
    private final Meal meal;

    public MealUpdatedEvent(Object source, Meal meal) {
        super(source);
        this.meal = meal;
    }
    public Meal getMeal() {
        return meal;
    }
}
