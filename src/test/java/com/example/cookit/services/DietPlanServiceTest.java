//package com.example.cookit.services;
//
//import com.example.cookit.DTO.DietPlanDto;
//import com.example.cookit.DTO.UpdateDietPlanDto;
//import com.example.cookit.entities.AppUser;
//import com.example.cookit.entities.DietPlan;
//import com.example.cookit.repositories.DietPlanRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class DietPlanServiceTest {
//    @InjectMocks
//    private DietPlanService dietPlanService;
//    @Mock
//    private DietPlanRepository dietPlanRepository;
//    @Mock
//    private AppUserService appUserService;
//
//    private AppUser appUser;
//    private DietPlan dietPlan;
//    private DietPlan dietPlan2;
//    private DietPlanDto dietPlanDto;
//    private UpdateDietPlanDto updateDietPlanDto;
//    private List<DietPlan> dietPlanList;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        appUser = new AppUser();
//        appUser.setId(UUID.fromString("555d02eb-d4f5-48f4-955c-55a82ab55e07"));
//        dietPlanDto = new DietPlanDto
//                ("testPlan",UUID.fromString("555d02eb-d4f5-48f4-955c-55a82ab55e07"),2000.00);
//        updateDietPlanDto = new UpdateDietPlanDto(UUID.randomUUID(),"testname2",1500.00);
//        dietPlan = new DietPlan();
//        dietPlan.setDailyCalories(2000.0);
//        dietPlan.setName("testPlan");
//        dietPlan.setAppUser(appUser);
//        dietPlan2 = new DietPlan();
//        dietPlan2.setName("testPlan2");
//        dietPlan2.setAppUser(appUser);
//        dietPlan2.setDailyCalories(1500.00);
//        dietPlanList = new ArrayList<>();
//        dietPlanList.add(dietPlan);
//        dietPlanList.add(dietPlan2);
//
//
//    }
//    @Test
//    void addDietPlanSuccess() {
//        UUID dietPlanId = UUID.randomUUID();
//
//        when(appUserService.userExists(dietPlanDto.appUserId())).thenReturn(true);
//        when(appUserService.getUserById(dietPlanDto.appUserId())).thenReturn(appUser);
//        when(dietPlanRepository.save(dietPlan)).thenAnswer(invocation -> {
//            DietPlan plan = invocation.getArgument(0);
//            plan.setId(dietPlanId);
//            return plan;
//        });
//
//        ResponseEntity<String> response = dietPlanService.addDietPlan(dietPlanDto);
//
//        ArgumentCaptor<DietPlan> captor = ArgumentCaptor.forClass(DietPlan.class);
//        verify(dietPlanRepository, times(1)).save(captor.capture());
//        verify(appUserService,times(1)).userExists(dietPlanDto.appUserId());
//        verify(appUserService,times(1)).getUserById(dietPlanDto.appUserId());
//
//        assertEquals(response.getStatusCode(), HttpStatus.CREATED);
//        assertEquals(response.getBody(),"Saved Diet plan"+dietPlan.getName());
//    }
//
//    @Test
//    void addDietPlanFailureUserNotFound() {
//
//        when(appUserService.userExists(dietPlanDto.appUserId())).thenReturn(false);
//        ResponseEntity<String> response = dietPlanService.addDietPlan(dietPlanDto);
//
//        verify(appUserService,times(1)).userExists(dietPlanDto.appUserId());
//        verify(appUserService,times(0)).getUserById(dietPlanDto.appUserId());
//        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
//        assertEquals(response.getBody(),"User with id:"+dietPlanDto.appUserId()+" not found");
//    }
//
//    @Test
//    void updateDietPlanSuccess() {
//        when(dietPlanRepository.existsById(updateDietPlanDto.id())).thenReturn(true);
//        when(dietPlanRepository.findById(updateDietPlanDto.id())).thenReturn(Optional.of(dietPlan));
//
//        ResponseEntity<String> response = dietPlanService.updateDietPlan(updateDietPlanDto);
//        ArgumentCaptor<DietPlan> captor = ArgumentCaptor.forClass(DietPlan.class);
//        verify(dietPlanRepository, times(1)).save(captor.capture());
//        verify(dietPlanRepository,times(1)).existsById(updateDietPlanDto.id());
//        verify(dietPlanRepository,times(1)).findById(updateDietPlanDto.id());
//        assertEquals(response.getStatusCode(), HttpStatus.OK);
//        assertEquals(response.getBody(),"Updated Diet plan :"+dietPlan.getName());
//    }
//    @Test
//    void updateDietPlanFailureDietPLanNotFound() {
//        when(dietPlanRepository.existsById(updateDietPlanDto.id())).thenReturn(false);
//
//        ResponseEntity<String> response = dietPlanService.updateDietPlan(updateDietPlanDto);
//
//        verify(dietPlanRepository,times(1)).existsById(updateDietPlanDto.id());
//        verify(dietPlanRepository,times(0)).findById(updateDietPlanDto.id());
//        verify(dietPlanRepository,times(0)).save(dietPlan);
//        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
//        assertEquals(response.getBody(), "Diet plan with id "+updateDietPlanDto.id()+"not found.");
//    }
//
//    @Test
//    void getByUserIdSuccess() {
//        UUID id = UUID.randomUUID();
//        DietPlanDto dietPlanDto1 = new DietPlanDto("testPlan",appUser.getId(),dietPlan.getDailyCalories());
//        DietPlanDto dietPlanDto2 = new DietPlanDto("testPlan2",appUser.getId(),dietPlan2.getDailyCalories());
//        List<DietPlanDto> dietPlanDtoList = new ArrayList<>();
//        dietPlanDtoList.add(dietPlanDto1);
//        dietPlanDtoList.add(dietPlanDto2);
//        when(appUserService.userExists(id)).thenReturn(true);
//        when(appUserService.getUserById(id)).thenReturn(appUser);
//        when(dietPlanRepository.findDietPlansByAppUser(appUser)).thenReturn(dietPlanList);
//
//        ResponseEntity<List<DietPlanDto>> response = dietPlanService.getDietPlansByUserId(id);
//
//        verify(appUserService,times(1)).userExists(id);
//        verify(appUserService,times(1)).getUserById(id);
//        verify(dietPlanRepository,times(1)).findDietPlansByAppUser(appUser);
//        assertEquals(response.getStatusCode(), HttpStatus.OK);
//        assertEquals(response.getBody(),dietPlanDtoList);
//    }
//    @Test
//    void getByUserIdFailure() {
//        UUID id = UUID.randomUUID();
//        when(appUserService.userExists(id)).thenReturn(false);
//
//        ResponseEntity<List<DietPlanDto>> response = dietPlanService.getDietPlansByUserId(id);
//
//        verify(appUserService,times(1)).userExists(id);
//        verify(appUserService,times(0)).getUserById(id);
//        verify(dietPlanRepository,times(0)).findDietPlansByAppUser(appUser);
//        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
//        assertEquals(response.getBody(),null);
//    }
//    @Test
//    void deleteDietPlanSuccess() {
//        UUID dietPlanId = UUID.randomUUID();
//        when(dietPlanRepository.existsById(dietPlanId)).thenReturn(true);
//
//        ResponseEntity<String> response = dietPlanService.deleteDietPlan(dietPlanId);
//
//        verify(dietPlanRepository,times(1)).existsById(dietPlanId);
//        verify(dietPlanRepository,times(1)).deleteById(dietPlanId);
//        assertEquals(response.getStatusCode(), HttpStatus.OK);
//        assertEquals(response.getBody(),"Deleted Diet plan with id:"+dietPlanId);
//    }
//
//    @Test
//    void deleteDietPlanFailureDietPlanNotFound() {
//        UUID dietPlanId = UUID.randomUUID();
//        when(dietPlanRepository.existsById(dietPlanId)).thenReturn(false);
//
//        ResponseEntity<String> response = dietPlanService.deleteDietPlan(dietPlanId);
//        verify(dietPlanRepository,times(1)).existsById(dietPlanId);
//        verify(dietPlanRepository,times(0)).deleteById(dietPlanId);
//        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
//        assertEquals(response.getBody(),"Diet plan with id "+dietPlanId+"not found");
//    }
//
//    @Test
//    void existByIdSuccess() {
//        UUID id = UUID.randomUUID();
//        when(dietPlanRepository.existsById(id)).thenReturn(true);
//
//        boolean exists = dietPlanRepository.existsById(id);
//        verify(dietPlanRepository,times(1)).existsById(id);
//        assertEquals(exists,true);
//    }
//    @Test
//    void existByIdFailure() {
//        UUID id = UUID.randomUUID();
//        when(dietPlanRepository.existsById(id)).thenReturn(false);
//
//        boolean exists = dietPlanRepository.existsById(id);
//        verify(dietPlanRepository,times(1)).existsById(id);
//        assertEquals(exists,false);
//    }
//}
