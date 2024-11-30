package com.telerikacademy.web.jobmatch.helpers;

import com.telerikacademy.web.jobmatch.models.Employer;
import com.telerikacademy.web.jobmatch.models.JobApplication;
import com.telerikacademy.web.jobmatch.models.Location;
import com.telerikacademy.web.jobmatch.models.Status;
import com.telerikacademy.web.jobmatch.models.dtos.EmployerDtoIn;
import com.telerikacademy.web.jobmatch.models.dtos.EmployerOutDto;
import com.telerikacademy.web.jobmatch.models.dtos.JobApplicationDtoIn;
import com.telerikacademy.web.jobmatch.models.dtos.JobApplicationDtoOut;
import com.telerikacademy.web.jobmatch.services.contracts.LocationService;
import com.telerikacademy.web.jobmatch.services.contracts.StatusService;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface JobApplicationMappers {
    JobApplicationMappers INSTANCE = Mappers.getMapper(JobApplicationMappers.class);

    @Mapping(source = "description", target = "description")
    @Mapping(source = "minSalary", target = "minSalary")
    @Mapping(source = "maxSalary", target = "maxSalary")
    @Mapping(expression = "java(returnInitialStatus(statusService))", target = "status")
    @Mapping(target = "location", source = "jobApplicationDtoIn", qualifiedByName = "mapLocation")
    @Mapping(source = "hybrid", target = "hybrid")
    JobApplication fromDtoIn(JobApplicationDtoIn jobApplicationDtoIn,
                             @Context StatusService statusService,
                             @Context LocationService locationService);

    @Mapping(source = "description", target = "description")
    @Mapping(source = "minSalary", target = "minSalary")
    @Mapping(source = "maxSalary", target = "maxSalary")
    @Mapping(source = "professional.firstName", target = "firstName")
    @Mapping(source = "professional.lastName", target = "lastName")
    @Mapping(source = "professional.email", target = "email")
    @Mapping(source = "jobApplication.location.name", target = "location")
    @Mapping(source = "hybrid", target = "hybrid")
    @Mapping(source = "jobApplication.status.status", target = "status")
    @Mapping(target = "qualifications", expression =
            "java(jobApplication.getMatchedJobAds() != null ? jobApplication.getMatchedJobAds() : new java.util.HashSet<>())")
    @Mapping(target = "matchedJobAds", expression =
            "java(jobApplication.getQualifications() != null ? jobApplication.getQualifications() : new java.util.HashSet<>())")
    JobApplicationDtoOut toDtoOut(JobApplication jobApplication);

    List<JobApplicationDtoOut> toDtoOutList(List<JobApplication> jobApplication);

    @Named("mapLocation")
    default Location mapLocation(JobApplicationDtoIn jobApplicationDtoIn, @Context LocationService locationService) {
        return locationService.returnIfExistOrCreate(jobApplicationDtoIn.getLocCountryIsoCode(),
                jobApplicationDtoIn.getLocCityId());
    }

    default Status returnInitialStatus(@Context StatusService statusService){
        return statusService.getStatus("Active");
    }
}
