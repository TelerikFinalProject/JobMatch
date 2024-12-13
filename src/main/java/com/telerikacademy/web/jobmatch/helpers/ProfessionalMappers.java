package com.telerikacademy.web.jobmatch.helpers;

import com.telerikacademy.web.jobmatch.models.*;
import com.telerikacademy.web.jobmatch.models.dtos.users.ProfessionalDtoIn;
import com.telerikacademy.web.jobmatch.models.dtos.users.ProfessionalOutDto;
import com.telerikacademy.web.jobmatch.models.dtos.users.ProfessionalUpdateDto;
import com.telerikacademy.web.jobmatch.models.dtos.users.mvc.EmployerDetailsDto;
import com.telerikacademy.web.jobmatch.models.dtos.users.mvc.ProfessionalDetailsDto;
import com.telerikacademy.web.jobmatch.services.contracts.LocationService;
import com.telerikacademy.web.jobmatch.services.contracts.StatusService;
import jakarta.validation.Valid;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProfessionalMappers {
    ProfessionalMappers INSTANCE = Mappers.getMapper(ProfessionalMappers.class);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "summary", target = "summary")
    @Mapping(target = "location", source = "professionalDtoIn", qualifiedByName = "mapLocation")
    @Mapping(target = "roles", expression = "java(returnInitialRole())")
    @Mapping(target = "status", expression = "java(returnInitialStatus(statusService))")
    Professional fromDtoIn(ProfessionalDtoIn professionalDtoIn,
                           @Context LocationService locationService,
                           @Context StatusService statusService);


    @Mapping(target = "id", source = "professional.id")
    @Mapping(target = "location", source = "professionalDtoIn", qualifiedByName = "mapLocation")
    @Mapping(target = "roles", source = "professional.roles")
    @Mapping(target = "username", source = "professionalDtoIn.username")
    @Mapping(target = "password", source = "professionalDtoIn.password")
    @Mapping(target = "email", source = "professionalDtoIn.email")
    @Mapping(target = "firstName", source = "professionalDtoIn.firstName")
    @Mapping(target = "lastName", source = "professionalDtoIn.lastName")
    @Mapping(target = "summary", source = "professionalDtoIn.summary")
    @Mapping(target = "successfulMatches", source = "professional.successfulMatches")
    @Mapping(target = "status", source = "professionalDtoIn",
            qualifiedByName = "returnUpdatedStatus")
    Professional fromDtoIn(Professional professional,
                           ProfessionalUpdateDto professionalDtoIn,
                           @Context LocationService locationService,
                           @Context StatusService statusService);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "summary", target = "summary")
    @Mapping(source = "roles", target = "roles") // Nested mapping for role
    @Mapping(source = "location.name", target = "location")// Nested mapping for location
    @Mapping(source = "status.status", target = "status")
    @Mapping(target = "successfulMatches", source = "professional.successfulMatches",
            qualifiedByName = "returnSuccessfulMatches")
    ProfessionalOutDto toDtoOut(Professional professional);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "email", target = "email")
    @Mapping(target = "roles", expression = "java(returnInitialRole())")
    @Mapping(target = "location", source = "professionalDtoIn", qualifiedByName = "mapLocation")
    UserPrincipal toUserPrinciple(ProfessionalDtoIn professionalDtoIn,
                                  @Context LocationService locationService);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "location.isoCode", target = "locCountryIsoCode")
    @Mapping(source = "location.id", target = "locCityId")
    @Mapping(source = "summary", target = "summary")
    ProfessionalDetailsDto toProfessionalDetailsDto(Professional professional);

    @Mapping(target = "id", source = "professional.id")
    @Mapping(target = "roles", source = "professional.roles")
    @Mapping(target = "username", source = "professionalDetailsDto.username")
    @Mapping(target = "email", source = "professionalDetailsDto.email")
    @Mapping(target = "summary", source = "professionalDetailsDto.summary")
    @Mapping(target = "firstName", source = "professionalDetailsDto.firstName")
    @Mapping(target = "lastName", source = "professionalDetailsDto.lastName")
    @Mapping(target = "password", source = "professional.password")
    @Mapping(target = "location", source = "professionalDetailsDto", qualifiedByName = "mapLocationFromDetailsDto")
    @Mapping(target = "successfulMatches", source = "professional.successfulMatches")
    Professional fromProfessionalDetailsDto(@Valid ProfessionalDetailsDto professionalDetailsDto, Professional professional, @Context LocationService locationService);


    List<ProfessionalOutDto> toDtoOutList(List<Professional> professionals);

    @Named("mapLocation")
    default Location mapLocation(ProfessionalDtoIn professionalDtoIn, @Context LocationService locationService) {
        return locationService.returnIfExistOrCreate(professionalDtoIn.getLocCountryIsoCode(),
                professionalDtoIn.getLocCityId());
    }

    default String returnInitialRole() {
        return "PROFESSIONAL";
    }

    default Status returnInitialStatus(@Context StatusService statusService) {
        return statusService.getStatus("Active");
    }

    @Named("returnUpdatedStatus")
    default Status returnUpdatedStatus(ProfessionalUpdateDto professionalUpdateDto,
                                       @Context StatusService statusService) {
        return statusService.getStatus(professionalUpdateDto.getUpdatedStatus());
    }

    @Named("returnSuccessfulMatches")
    default String returnSuccessfulMatches(Set<Employer> successfulMatches) {
        if (successfulMatches != null) {
            List<String> companyNames = new ArrayList<>();

            for (Employer employer : successfulMatches) {
                companyNames.add(employer.getCompanyName());
            }

            return String.join(", ", companyNames);
        }
        return "No successful matches.";
    }

    @Named("mapLocationFromDetailsDto")
    default Location mapLocationFromDetailsDto(ProfessionalDetailsDto professionalDetailsDto, @Context LocationService locationService) {
        return locationService.returnIfExistOrCreate(professionalDetailsDto.getLocCountryIsoCode(),
                professionalDetailsDto.getLocCityId());
    }
}
