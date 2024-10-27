package com.example.cookit.events;

import com.example.cookit.entities.Ingredient;
import com.example.cookit.entities.Meal;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class EventPublisher{

    ApplicationEventPublisher publisher;
    public EventPublisher(ApplicationEventPublisher publisher){
        this.publisher = publisher;
    }
    public void publishIngredientUpdated(Ingredient ingredient){
        publisher.publishEvent(new IngredientUpdatedEvent(this,ingredient));;
    }
    public void publishMealUpdatedEvent(Meal meal){
        publisher.publishEvent(new MealUpdatedEvent(this,meal));
    }
}
