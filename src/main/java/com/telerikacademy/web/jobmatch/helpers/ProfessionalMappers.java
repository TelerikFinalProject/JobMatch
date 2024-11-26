package com.telerikacademy.web.jobmatch.helpers;

import com.telerikacademy.web.jobmatch.models.Professional;
import com.telerikacademy.web.jobmatch.models.dtos.ProfessionalDtoIn;
import com.telerikacademy.web.jobmatch.models.dtos.ProfessionalOutDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProfessionalMappers {
    ProfessionalMappers INSTANCE = Mappers.getMapper(ProfessionalMappers.class);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "summary", target = "summary")
    Professional fromDtoIn(ProfessionalDtoIn professionalDtoIn);


    @Mapping(target = "id", source = "professional.id")
    @Mapping(target = "location", source = "professional.location")
    @Mapping(target = "role", source = "professional.role")
    @Mapping(target = "username", source = "professionalDtoIn.username")
    @Mapping(target = "password", source = "professionalDtoIn.password")
    @Mapping(target = "email", source = "professionalDtoIn.email")
    @Mapping(target = "firstName", source = "professionalDtoIn.firstName")
    @Mapping(target = "lastName", source = "professionalDtoIn.lastName")
    @Mapping(target = "summary", source = "professionalDtoIn.summary")
    Professional fromDtoIn(Professional professional, ProfessionalDtoIn professionalDtoIn);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "summary", target = "summary")
    @Mapping(source = "role.role", target = "role") // Nested mapping for role
    @Mapping(source = "location.name", target = "location") // Nested mapping for location
    ProfessionalOutDto toDtoOut(Professional professional);

    List<ProfessionalOutDto> toDtoOutList(List<Professional> professionals);
}
