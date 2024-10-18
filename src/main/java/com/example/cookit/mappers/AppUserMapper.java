package com.example.cookit.mappers;

import com.example.cookit.DTO.RegisterDto;
import com.example.cookit.entities.AppUser;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AppUserMapper {
    AppUserMapper INSTANCE = Mappers.getMapper(AppUserMapper.class);

    AppUser toEntity (RegisterDto registerDto);
    RegisterDto toDto(AppUser appUser);
}
