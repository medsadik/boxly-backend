package org.example.boxlybackend.mapper;

import org.example.boxlybackend.dto.WeeklySubscriptionRequest;
import org.example.boxlybackend.dto.WeeklySubscriptionResponse;
import org.example.boxlybackend.entites.WeeklySubscription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = EmployeMapper.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WeeklySubscriptionMapper {

    WeeklySubscriptionResponse toResponse(WeeklySubscription subscription);

    WeeklySubscription toEntity(WeeklySubscriptionRequest request);

}