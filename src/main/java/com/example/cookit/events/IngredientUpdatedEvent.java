package com.example.cookit.events;

import com.example.cookit.entities.Ingredient;
import org.springframework.context.ApplicationEvent;

public class IngredientUpdatedEvent extends ApplicationEvent {
    private final Ingredient ingredient;
    public IngredientUpdatedEvent(Object source, Ingredient ingredient) {
        super(source);
        this.ingredient = ingredient;
    }
    public Ingredient getIngredient() {
        return ingredient;
    }

}
