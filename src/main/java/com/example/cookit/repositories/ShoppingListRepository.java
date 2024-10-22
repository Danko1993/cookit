package com.example.cookit.repositories;

import com.example.cookit.entities.ShoppingList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ShoppingListRepository extends JpaRepository<ShoppingList, UUID> {


}
