package com.example.cookit.mappers;

import com.example.cookit.DTO.ReadyShopingListDto;
import com.example.cookit.DTO.ShoppingListDto;
import com.example.cookit.entities.ShoppingList;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ShoppingListMapper {
    ShoppingListMapper INSTANCE = Mappers.getMapper(ShoppingListMapper.class);

    @Mapping(target = "appUser.id", source = "appUserId")
    @Mapping(target = "ingredientsWithWeight", ignore = true)
    ShoppingList toEntity(ShoppingListDto shoppingListDto);
    @Mapping(target = "ingredientsWithWeight", ignore = true)
    ReadyShopingListDto toReadyShopingListDto(ShoppingList shoppingList);
}
