package com.telerikacademy.web.jobmatch.helpers;

import com.telerikacademy.web.jobmatch.models.Employer;
import com.telerikacademy.web.jobmatch.models.dtos.EmployerOutDto;
import com.telerikacademy.web.jobmatch.models.dtos.EmployerDtoIn;
import com.telerikacademy.web.jobmatch.models.dtos.EmployerUpdateDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EmployerMappers {
    EmployerMappers INSTANCE = Mappers.getMapper(EmployerMappers.class);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "companyName", target = "companyName")
    @Mapping(source = "description", target = "description")
    Employer fromDtoIn(EmployerDtoIn employerDtoIn);

    @Mapping(target = "id", source = "employer.id") // Keep the `id` from the existing Employer
    @Mapping(target = "location", source = "employer.location") // Keep the `location` from the existing Employer
    @Mapping(target = "role", source = "employer.role") // Keep the `role` from the existing Employer
    @Mapping(target = "username", source = "employerDtoIn.username") // Update from EmployerDtoIn
    @Mapping(target = "password", source = "employerDtoIn.password") // Update from EmployerDtoIn
    @Mapping(target = "email", source = "employerDtoIn.email") // Update from EmployerDtoIn
    @Mapping(target = "companyName", source = "employerDtoIn.companyName") // Update from EmployerDtoIn
    @Mapping(target = "description", source = "employerDtoIn.description") // Update from EmployerDtoIn
    Employer fromDtoIn(Employer employer, EmployerUpdateDto employerDtoIn);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "companyName", target = "companyName")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "role.role", target = "role") // Nested mapping for role
    @Mapping(source = "location.name", target = "location") // Nested mapping for location
    EmployerOutDto toDtoOut(Employer employer);

    List<EmployerOutDto> toDtoOutList(List<Employer> employers);
}
