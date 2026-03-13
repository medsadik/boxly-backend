package org.example.boxlybackend.mapper;

import org.example.boxlybackend.dto.MenuOptionRequest;
import org.example.boxlybackend.dto.MenuOptionResponse;
import org.example.boxlybackend.entites.MenuOption;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface MenuOptionMapper {

    // Request → Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "menuWeekDayOptions", ignore = true)
    @Mapping(target = "reservations", ignore = true)
    MenuOption toEntity(MenuOptionRequest request);

    // Entity → Request
    MenuOptionRequest toRequest(MenuOption entity);

    @Mapping(target = "createdAt",source = "createdAt")
    MenuOptionResponse toResponse(MenuOption entity);

    // Update existing entity (for PUT)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "menuWeekDayOptions", ignore = true)
    @Mapping(target = "reservations", ignore = true)
    void updateEntityFromRequest(MenuOptionRequest request, @MappingTarget MenuOption entity);
}
