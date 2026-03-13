package org.example.boxlybackend.mapper;

import org.example.boxlybackend.dto.MenuWeekDayResponse;
import org.example.boxlybackend.entites.MenuWeekDay;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = MenuWeekDayOptionMapper.class
)
public interface MenuWeekDayMapper {
    @Mapping(source = "options", target = "options")

    MenuWeekDayResponse toResponse(MenuWeekDay entity);
}