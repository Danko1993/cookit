package com.example.cookit.mappers;

import com.example.cookit.DTO.AppUserDto;
import com.example.cookit.entities.AppUser;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AppUserMapper {
    AppUserMapper INSTANCE = Mappers.getMapper(AppUserMapper.class);

    AppUser toEntity (AppUserDto appUserDto);
    AppUserDto toDto(AppUser appUser);
}
