package com.telerikacademy.web.jobmatch.helpers;

import com.telerikacademy.web.jobmatch.models.JobAd;
import com.telerikacademy.web.jobmatch.models.Location;
import com.telerikacademy.web.jobmatch.models.Status;
import com.telerikacademy.web.jobmatch.models.dtos.JobAdDtoIn;
import com.telerikacademy.web.jobmatch.models.dtos.JobAdDtoOut;
import com.telerikacademy.web.jobmatch.services.contracts.LocationService;
import com.telerikacademy.web.jobmatch.services.contracts.StatusService;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface JobAdMappers {
    JobAdMappers INSTANCE = Mappers.getMapper(JobAdMappers.class);


    @Mapping(source = "positionTitle", target = "positionTitle")
    @Mapping(source = "minSalary", target = "minSalary")
    @Mapping(source = "maxSalary", target = "maxSalary")
    @Mapping(source = "jobDescription", target = "jobDescription")
    @Mapping(source = "hybrid", target = "hybrid")
    @Mapping(target = "location", source = "jobAdDtoIn", qualifiedByName = "mapLocation")
    @Mapping(target = "status", expression = "java(returnInitialStatus(statusService))")
    JobAd fromDtoIn(JobAdDtoIn jobAdDtoIn,
                    @Context LocationService locationService,
                    @Context StatusService statusService);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "positionTitle", target = "positionTitle")
    @Mapping(source = "minSalary", target = "minSalary")
    @Mapping(source = "maxSalary", target = "maxSalary")
    @Mapping(source = "jobDescription", target = "jobDescription")
    @Mapping(target = "city", source = "jobAd.location.name") // Nested mapping for role
    @Mapping(source = "jobAd.employer.companyName", target = "companyName")// Nested mapping for location
    @Mapping(target = "hybrid", source = "hybrid")
    JobAdDtoOut toDtoOut(JobAd jobAd);

    List<JobAdDtoOut> toDtoOutList(List<JobAd> jobAds);

    Set<JobAdDtoOut> toDtoOutSet(Set<JobAd> jobAds);

    @Named("mapLocation")
    default Location mapLocation(JobAdDtoIn jobAdDtoIn, @Context LocationService locationService) {
        return locationService.returnIfExistOrCreate(jobAdDtoIn.getLocCountryIsoCode(),
                jobAdDtoIn.getLocCityId());
    }

    default Status returnInitialStatus(@Context StatusService statusService){
        return statusService.getStatus("Active");
    }

}
