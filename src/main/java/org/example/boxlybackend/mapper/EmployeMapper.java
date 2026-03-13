package org.example.boxlybackend.mapper;

import org.example.boxlybackend.dto.EmployeDTO;
import org.example.boxlybackend.entites.Employe;
import org.mapstruct.Mapper;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmployeMapper {

    EmployeDTO toDto(Employe employe);

    Employe toEntity(EmployeDTO dto);
}