package com.telerikacademy.web.jobmatch.helpers;

import com.telerikacademy.web.jobmatch.models.Location;
import com.telerikacademy.web.jobmatch.models.Professional;
import com.telerikacademy.web.jobmatch.models.Status;
import com.telerikacademy.web.jobmatch.models.dtos.ProfessionalDtoIn;
import com.telerikacademy.web.jobmatch.models.dtos.ProfessionalOutDto;
import com.telerikacademy.web.jobmatch.models.dtos.ProfessionalUpdateDto;
import com.telerikacademy.web.jobmatch.services.contracts.LocationService;
import com.telerikacademy.web.jobmatch.services.contracts.StatusService;
import org.mapstruct.*;
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
    @Mapping(target = "location", source = "professionalDtoIn", qualifiedByName = "mapLocation")
    @Mapping(target = "status", expression = "java(returnInitialStatus(statusService))")
    Professional fromDtoIn(ProfessionalDtoIn professionalDtoIn,
                           @Context LocationService locationService,
                           @Context StatusService statusService);


    @Mapping(target = "id", source = "professional.id")
    @Mapping(target = "location", source = "professionalDtoIn", qualifiedByName = "mapLocation")
    @Mapping(target = "username", source = "professionalDtoIn.username")
    @Mapping(target = "password", source = "professionalDtoIn.password")
    @Mapping(target = "email", source = "professionalDtoIn.email")
    @Mapping(target = "firstName", source = "professionalDtoIn.firstName")
    @Mapping(target = "lastName", source = "professionalDtoIn.lastName")
    @Mapping(target = "summary", source = "professionalDtoIn.summary")
    @Mapping(target = "status", source = "professional.status")
    Professional fromDtoIn(Professional professional,
                           ProfessionalUpdateDto professionalDtoIn,
                           @Context LocationService locationService);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "summary", target = "summary") // Nested mapping for role
    @Mapping(source = "location.name", target = "location")// Nested mapping for location
    @Mapping(source = "status.status", target = "status")
    ProfessionalOutDto toDtoOut(Professional professional);

    List<ProfessionalOutDto> toDtoOutList(List<Professional> professionals);

    @Named("mapLocation")
    default Location mapLocation(ProfessionalDtoIn professionalDtoIn, @Context LocationService locationService) {
        return locationService.returnIfExistOrCreate(professionalDtoIn.getLocCountryIsoCode(),
                professionalDtoIn.getLocCityId());
    }

    default Status returnInitialStatus(@Context StatusService statusService){
        return statusService.getStatus("Active");
    }
}
