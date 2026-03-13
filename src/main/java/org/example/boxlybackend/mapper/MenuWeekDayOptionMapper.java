package org.example.boxlybackend.mapper;

import org.example.boxlybackend.dto.MenuWeekDayOptionResponse;
import org.example.boxlybackend.entites.MenuWeekDayOption;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = MenuOptionMapper.class
)
public interface MenuWeekDayOptionMapper {
    @Mapping(source = "defaultOption", target = "defaultOption")
    MenuWeekDayOptionResponse toResponse(MenuWeekDayOption entity);
}