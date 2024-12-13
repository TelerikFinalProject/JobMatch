package com.telerikacademy.web.jobmatch.helpers;

import com.telerikacademy.web.jobmatch.models.Employer;
import com.telerikacademy.web.jobmatch.models.Location;
import com.telerikacademy.web.jobmatch.models.UserPrincipal;
import com.telerikacademy.web.jobmatch.models.dtos.users.EmployerOutDto;
import com.telerikacademy.web.jobmatch.models.dtos.users.EmployerDtoIn;
import com.telerikacademy.web.jobmatch.models.dtos.users.EmployerUpdateDto;
import com.telerikacademy.web.jobmatch.models.dtos.users.mvc.EmployerDetailsDto;
import com.telerikacademy.web.jobmatch.services.contracts.LocationService;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EmployerMappers {
    EmployerMappers INSTANCE = Mappers.getMapper(EmployerMappers.class);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "companyName", target = "companyName")
    @Mapping(target = "location", source = "employerDtoIn", qualifiedByName = "mapLocation")
    @Mapping(target = "roles", expression = "java(returnInitialRole())")
    @Mapping(source = "description", target = "description")
    Employer fromDtoIn(EmployerDtoIn employerDtoIn,
                       @Context LocationService locationService);

    @Mapping(target = "id", source = "employer.id") // Keep the `id` from the existing Employer
    @Mapping(target = "roles", source = "employer.roles") // Keep the `role` from the existing Employer
    @Mapping(target = "username", source = "employerDtoIn.username") // Update from EmployerDtoIn
    @Mapping(target = "password", source = "employerDtoIn.password") // Update from EmployerDtoIn
    @Mapping(target = "email", source = "employerDtoIn.email") // Update from EmployerDtoIn
    @Mapping(target = "companyName", source = "employerDtoIn.companyName") // Update from EmployerDtoIn
    @Mapping(target = "description", source = "employerDtoIn.description") // Update from EmployerDtoIn
    @Mapping(target = "location", source = "employerDtoIn", qualifiedByName = "mapLocation")
    @Mapping(target = "successfulProfessionalsMatched", source = "employer.successfulProfessionalsMatched")
    Employer fromDtoIn(Employer employer, EmployerUpdateDto employerDtoIn, @Context LocationService locationService);

    @Mapping(target = "id", source = "employer.id")
    @Mapping(target = "roles", source = "employer.roles")
    @Mapping(target = "username", source = "employerDetailsDto.username")
    @Mapping(target = "email", source = "employerDetailsDto.email")
    @Mapping(target = "description", source = "employerDetailsDto.description")
    @Mapping(target = "companyName", source = "employerDetailsDto.companyName")
    @Mapping(target = "password", source = "employer.password")
    @Mapping(target = "location", source = "employerDetailsDto", qualifiedByName = "mapLocationFromDetailsDto")
    @Mapping(target = "successfulProfessionalsMatched", source = "employer.successfulProfessionalsMatched")
    Employer fromEmployerDetailsDto(EmployerDetailsDto employerDetailsDto, Employer employer, @Context LocationService locationService);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "companyName", target = "companyName")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "location.isoCode", target = "locCountryIsoCode")
    @Mapping(source = "location.id", target = "locCityId")
    @Mapping(source = "description", target = "description")
    EmployerDetailsDto toEmployerDetailsDto(Employer employer);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "companyName", target = "companyName")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "location.isoCode", target = "locCountryIsoCode")
    @Mapping(source = "location.id", target = "locCityId")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "password", target = "password")
    EmployerDtoIn toDtoIn(Employer employer);


    @Mapping(source = "username", target = "username")
    @Mapping(source = "companyName", target = "companyName")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "roles", target = "roles") // Nested mapping for role
    @Mapping(source = "location.name", target = "location")// Nested mapping for location
    @Mapping(target = "successfulMatches", expression =
            "java(employer.getSuccessfulProfessionalsMatched() != null ? employer.getSuccessfulProfessionalsMatched().size() : 0)")
    EmployerOutDto toDtoOut(Employer employer);


    @Mapping(source = "username", target = "username")
    @Mapping(source = "email", target = "email")
    @Mapping(target = "roles", expression = "java(returnInitialRole())")
    @Mapping(target = "location", source = "employerDtoIn", qualifiedByName = "mapLocation")
    //@Mapping(source = "password", target = "password")
    UserPrincipal toUserPrinciple(EmployerDtoIn employerDtoIn,
                                  @Context LocationService locationService);

    List<EmployerOutDto> toDtoOutList(List<Employer> employers);

    @Named("mapLocation")
    default Location mapLocation(EmployerDtoIn employerDtoIn, @Context LocationService locationService) {
        return locationService.returnIfExistOrCreate(employerDtoIn.getLocCountryIsoCode(),
                employerDtoIn.getLocCityId());
    }

    @Named("mapLocationFromDetailsDto")
    default Location mapLocationFromDetailsDto(EmployerDetailsDto employerDetailsDto, @Context LocationService locationService) {
        return locationService.returnIfExistOrCreate(employerDetailsDto.getLocCountryIsoCode(),
                employerDetailsDto.getLocCityId());
    }

    default String returnInitialRole(){
        return "EMPLOYER";
    }
}
