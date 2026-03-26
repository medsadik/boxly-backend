package org.example.boxlybackend.mapper;

import org.example.boxlybackend.dto.CancellationResponse;
import org.example.boxlybackend.entites.CancellationRequest;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CancellationMapper {


    CancellationResponse toResponse(CancellationRequest request);
    List<CancellationResponse> toResponseList(List<CancellationRequest> requests);
}
