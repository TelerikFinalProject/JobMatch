package com.telerikacademy.web.jobmatch.helpers;

import com.telerikacademy.web.jobmatch.models.Employer;
import com.telerikacademy.web.jobmatch.models.Location;
import com.telerikacademy.web.jobmatch.models.dtos.EmployerOutDto;
import com.telerikacademy.web.jobmatch.models.dtos.EmployerDtoIn;
import com.telerikacademy.web.jobmatch.models.dtos.EmployerUpdateDto;
import com.telerikacademy.web.jobmatch.services.contracts.LocationService;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EmployerMappers {
    EmployerMappers INSTANCE = Mappers.getMapper(EmployerMappers.class);

    @Mapping(source = "username", target = "username")
    @Mapping(target = "password", source = "password")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "companyName", target = "companyName")
    @Mapping(target = "location", source = "employerDtoIn", qualifiedByName = "mapLocation")
    @Mapping(source = "description", target = "description")
    @Mapping(target = "roles", expression = "java(getDefaultRole())")
    Employer fromDtoIn(EmployerDtoIn employerDtoIn,
                       @Context LocationService locationService);

    @Mapping(target = "id", source = "employer.id") // Keep the `id` from the existing Employer
    @Mapping(target = "username", source = "employerDtoIn.username") // Update from EmployerDtoIn
    @Mapping(target = "password", source = "employerDtoIn.password") // Update from EmployerDtoIn
    @Mapping(target = "email", source = "employerDtoIn.email") // Update from EmployerDtoIn
    @Mapping(target = "companyName", source = "employerDtoIn.companyName") // Update from EmployerDtoIn
    @Mapping(target = "description", source = "employerDtoIn.description") // Update from EmployerDtoIn
    @Mapping(target = "location", source = "employerDtoIn", qualifiedByName = "mapLocation")
    @Mapping(target = "roles", expression = "java(getDefaultRole())")
    Employer fromDtoIn(Employer employer, EmployerUpdateDto employerDtoIn, @Context LocationService locationService);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "companyName", target = "companyName")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "location.name", target = "location") // Nested mapping for location
    EmployerOutDto toDtoOut(Employer employer);

    List<EmployerOutDto> toDtoOutList(List<Employer> employers);

    @Named("mapLocation")
    default Location mapLocation(EmployerDtoIn employerDtoIn, @Context LocationService locationService) {
        return locationService.returnIfExistOrCreate(employerDtoIn.getLocCountryIsoCode(),
                employerDtoIn.getLocCityId());
    }

    default String getDefaultRole() {
        return "ROLE_EMPLOYER";
    }
}
