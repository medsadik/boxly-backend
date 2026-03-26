package org.example.boxlybackend.services;


import org.example.boxlybackend.dto.MenuWeekDayResponse;

import java.time.LocalDate;
import java.util.List;

public interface MenuWeekDayService {

//    MenuWeekDayResponse addDay(Long weekId, MenuWeekDayRequest request);
//
//    MenuWeekDayResponse updateDay(Long weekId, Long dayId, MenuWeekDayRequest request);
//
//    MenuWeekDayResponse getById(Long weekId, Long dayId);
//
//    List<MenuWeekDayResponse> getAllByWeek(Long weekId);
//
//    void removeDay(Long weekId, Long dayId);

    // Option management
    MenuWeekDayResponse assignOption(Long optionId, LocalDate date);
    void removeOption(Long optionId, LocalDate date);
    MenuWeekDayResponse getMenuByDate(LocalDate date);
    List<MenuWeekDayResponse> getWeekMenu(LocalDate startDate);
    void generateMenuWeekDaysFromStartToToday(LocalDate startDate,LocalDate endDate);
    //    MenuWeekDayResponse removeOption(Long weekId, Long dayId, Long optionId);
//
//    MenuWeekDayResponse setDefaultOption(Long weekId, Long dayId, Long optionId);
}