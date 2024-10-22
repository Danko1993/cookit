package com.example.cookit.services;

import com.example.cookit.DTO.DietPlanDto;
import com.example.cookit.DTO.UpdateDietPlanDto;
import com.example.cookit.entities.AppUser;
import com.example.cookit.entities.DietPlan;
import com.example.cookit.mappers.DietPlanMapper;
import com.example.cookit.repositories.AppUserRepository;
import com.example.cookit.repositories.DietPlanRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DietPlanService {

    private final DietPlanMapper dietPlanMapper= DietPlanMapper.INSTANCE;
    @Autowired
    private  DietPlanRepository dietPlanRepository;
    @Autowired
    private AppUserRepository appUserRepository;

    @Transactional
    public ResponseEntity<String> addDietPlan(DietPlanDto dietPlanDto) {
        if (appUserRepository.findAppUserById(dietPlanDto.appUserId())==null){
            log.warn("User with id :{} not found", dietPlanDto.appUserId());
            return new ResponseEntity<>
                    ("User with id:"+dietPlanDto.appUserId()+" not found", HttpStatus.NOT_FOUND);
        }
        log.info("Starting saving Diet plan : {}", dietPlanDto.name());
        DietPlan dietPlan = dietPlanMapper.toEntity(dietPlanDto);
        dietPlan.setAppUser(appUserRepository.findAppUserById(dietPlanDto.appUserId()));
        dietPlanRepository.save(dietPlan);
        log.info("Saved Diet plan : {}", dietPlan.getName());
        return new ResponseEntity<>("Saved Diet plan"+dietPlan.getName(), HttpStatus.CREATED);
    }

    @Transactional
    public ResponseEntity<String> updateDietPlan(UpdateDietPlanDto updateDietPlanDto) {
        log.info("Checking if diet plan with id {} exists", updateDietPlanDto.id());
        if (dietPlanRepository.existsById(updateDietPlanDto.id())) {
            DietPlan existingDietPlan = dietPlanRepository.findById(updateDietPlanDto.id()).get();
            if (updateDietPlanDto.name() != existingDietPlan.getName()) {
                existingDietPlan.setName(updateDietPlanDto.name());
            }
            if (updateDietPlanDto.dailyCalories() != existingDietPlan.getDailyCalories()) {
                existingDietPlan.setDailyCalories(updateDietPlanDto.dailyCalories());
            }
            dietPlanRepository.save(existingDietPlan);
            log.info("Updated Diet plan : {}", existingDietPlan.getName());
            return new ResponseEntity<>("Updated Diet plan :"+existingDietPlan.getName(), HttpStatus.OK);
        }
        log.warn("Diet plan with id {} not found", updateDietPlanDto.id());
        return new ResponseEntity<>("Diet plan with id "+updateDietPlanDto.id()+"not found.", HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<List<DietPlanDto>> getDietPlansByUserId(UUID userId) {
        log.info("Checking if user with id {} exists", userId);
        if (appUserRepository.findAppUserById(userId)==null){
            log.warn("User with id :{} not found", userId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        log.info("User with id {} found", userId);
        AppUser appUser = appUserRepository.findAppUserById(userId);
        List<DietPlan> dietPlans = dietPlanRepository.findDietPlansByAppUser(appUser);
        List<DietPlanDto> result = dietPlans.stream().
                map(dietPlan -> dietPlanMapper.toDto(dietPlan)
                ).collect(Collectors.toList());

        log.info("Diet plan list : {}", result);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    public ResponseEntity<String> deleteDietPlan(UUID id) {
        log.info("Checking if diet plan with id {} exists", id);
        if (dietPlanRepository.existsById(id)) {
            dietPlanRepository.deleteById(id);
            log.info("Deleted Diet plan with id: {}", id);
            return new ResponseEntity<>("Deleted Diet plan with id:"+id, HttpStatus.OK);
        }
        log.warn("Diet plan with id {} not found", id);
        return new ResponseEntity<>("Diet plan with id "+id+"not found", HttpStatus.NOT_FOUND);
    }
}
