package org.example.boxlybackend.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.boxlybackend.dto.MenuWeekDayResponse;
import org.example.boxlybackend.entites.MenuOption;
import org.example.boxlybackend.entites.MenuWeek;
import org.example.boxlybackend.entites.MenuWeekDay;
import org.example.boxlybackend.entites.MenuWeekDayOption;
import org.example.boxlybackend.mapper.MenuWeekDayMapper;
import org.example.boxlybackend.repository.MenuOptionRepository;
import org.example.boxlybackend.repository.MenuWeekDayOptionRepository;
import org.example.boxlybackend.repository.MenuWeekDayRepository;
import org.example.boxlybackend.repository.MenuWeekRepository;
import org.example.boxlybackend.services.MenuWeekDayService;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MenuWeekDayServiceImpl implements MenuWeekDayService {

    private final MenuWeekRepository menuWeekRepository;
    private final MenuWeekDayRepository menuWeekDayRepository;
    private final MenuWeekDayOptionRepository menuWeekDayOptionRepository;
    private final MenuOptionRepository menuOptionRepository;
    private final MenuWeekDayMapper menuWeekMapper;
    private final Random random = new Random();

    // ── Option management ────────────────────────────────────────────────────

    @Override
    public MenuWeekDayResponse assignOption(Long optionId, LocalDate date) {

        MenuWeekDay day = menuWeekDayRepository.findByDate(date)
                .orElseGet(() -> menuWeekDayRepository.save(
                        MenuWeekDay.builder()
                                .date(date)
                                .active(true)
                                .build()
                ));
        MenuOption option = findOptionById(optionId);

        if (menuWeekDayOptionRepository.existsByMenuWeekDayDateAndMenuOptionId(date, optionId)) {
            throw new RuntimeException("This option is already assigned to this day");
        }

        MenuWeekDayOption dayOption = new MenuWeekDayOption();
        dayOption.setMenuWeekDay(day);
        dayOption.setMenuOption(option);

        dayOption.setDefaultOption(day.getOptions().isEmpty());

        menuWeekDayOptionRepository.save(dayOption);
        day.addOption(dayOption);
        MenuWeekDay dayWithOptions = findDayWithOptions(date);
        return menuWeekMapper.toResponse(dayWithOptions);
    }

    @Transactional
    @Override
    public void removeOption(Long optionId, LocalDate date) {

        MenuWeekDay day = menuWeekDayRepository.findByDate(date)
                .orElseThrow(() -> new RuntimeException("Menu day not found"));

        MenuWeekDayOption relation = menuWeekDayOptionRepository
                .findByMenuWeekDayDateAndMenuOptionId(date, optionId)
                .orElseThrow(() -> new RuntimeException("Option not assigned to this day"));

        day.removeOption(relation);

        menuWeekDayRepository.save(day);
    }
    @Override
    public MenuWeekDayResponse getMenuByDate(LocalDate date) {
        MenuWeekDay day = menuWeekDayRepository.findByDate(date)
                .orElseThrow(() -> new RuntimeException(
                        "No menu found for date " + date
                ));
        return menuWeekMapper.toResponse(day);
    }


    @Transactional
    public List<MenuWeekDayResponse> getWeekMenu(LocalDate startDate) {

        LocalDate startOfWeek = startDate.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = startDate.with(DayOfWeek.FRIDAY);

        List<MenuWeekDay> weekMenus = menuWeekDayRepository
                .findByDateBetweenOrderByDateAsc(startOfWeek, endOfWeek);

        return weekMenus.stream()
                .map(menuWeekMapper::toResponse)
                .collect(Collectors.toList());
    }

    private MenuWeek findWeekById(Long id) {
        return menuWeekRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MenuWeek not found with id: " + id));
    }

    private Optional<MenuWeekDay> findDayById(LocalDate date) {

        return menuWeekDayRepository.findByDate(date);
    }

    private MenuWeekDay findDayWithOptions(LocalDate date) {
        return menuWeekDayRepository.findByDate(date)
                .orElseThrow(() -> new RuntimeException("MenuWeekDay not found with date: " + date));
    }

    private MenuOption findOptionById(Long optionId) {
        return menuOptionRepository.findById(optionId)
                .orElseThrow(() -> new RuntimeException("MenuOption not found with id: " + optionId));
    }

    public void generateMenuWeekDaysFromStartToToday(LocalDate startDate) {

        List<MenuOption> allOptions = menuOptionRepository.findAll();
        if (allOptions.isEmpty()) {
            throw new RuntimeException("No menu options found. Please add some options first.");
        }

        LocalDate today = LocalDate.now();
        LocalDate current = startDate;

        while (!current.isAfter(today)) {

            if (current.getDayOfWeek() == DayOfWeek.SATURDAY ||
                    current.getDayOfWeek() == DayOfWeek.SUNDAY) {
                current = current.plusDays(1);
                continue;
            }

            // Skip if already exists
            if (menuWeekDayRepository.findByDate(current).isPresent()) {
                current = current.plusDays(1);
                continue;
            }

            MenuWeekDay day = MenuWeekDay.builder()
                    .date(current)
                    .active(true)
                    .build();

            // Assign 1-3 random options per day
            int optionsCount = 1 + random.nextInt(Math.min(3, allOptions.size()));
            List<MenuOption> shuffledOptions = new ArrayList<>(allOptions);
            Collections.shuffle(shuffledOptions);

            // Take first `optionsCount` options
            List<MenuOption> selectedOptions = shuffledOptions.subList(0, optionsCount);

            // Mark first one as default
            for (int i = 0; i < selectedOptions.size(); i++) {
                MenuOption option = selectedOptions.get(i);
                MenuWeekDayOption dayOption = MenuWeekDayOption.builder()
                        .menuWeekDay(day)
                        .menuOption(option)
                        .defaultOption(i == 0) // ✅ first one is default
                        .build();
                day.addOption(dayOption);
            }

            menuWeekDayRepository.save(day);

            current = current.plusDays(1);
        }
    }
}
