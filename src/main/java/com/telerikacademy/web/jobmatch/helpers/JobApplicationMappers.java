package com.telerikacademy.web.jobmatch.helpers;

import com.telerikacademy.web.jobmatch.exceptions.EntityStatusException;
import com.telerikacademy.web.jobmatch.models.*;
import com.telerikacademy.web.jobmatch.models.dtos.JobAdDtoOut;
import com.telerikacademy.web.jobmatch.models.dtos.JobApplicationDtoIn;
import com.telerikacademy.web.jobmatch.models.dtos.JobApplicationDtoOut;
import com.telerikacademy.web.jobmatch.models.dtos.JobApplicationDtoUpdate;
import com.telerikacademy.web.jobmatch.services.contracts.LocationService;
import com.telerikacademy.web.jobmatch.services.contracts.SkillService;
import com.telerikacademy.web.jobmatch.services.contracts.StatusService;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface JobApplicationMappers {
    JobApplicationMappers INSTANCE = Mappers.getMapper(JobApplicationMappers.class);

    @Mapping(source = "description", target = "description")
    @Mapping(source = "minSalary", target = "minSalary")
    @Mapping(source = "maxSalary", target = "maxSalary")
    @Mapping(target = "location", source = "jobApplicationDtoIn", qualifiedByName = "mapLocation")
    @Mapping(source = "hybrid", target = "hybrid")
    @Mapping(source = "skills", target = "qualifications", qualifiedByName = "mapSkills")
    @Mapping(target = "status", expression = "java(returnInitialStatus(statusService))")
    JobApplication fromDtoIn(JobApplicationDtoIn jobApplicationDtoIn,
                             @Context StatusService statusService,
                             @Context LocationService locationService,
                             @Context SkillService skillService);

    @Mapping(source = "jobApplication.id", target = "id")
    @Mapping(source = "jobApplicationDtoUpdate.description", target = "description")
    @Mapping(source = "jobApplicationDtoUpdate.minSalary", target = "minSalary")
    @Mapping(source = "jobApplicationDtoUpdate.maxSalary", target = "maxSalary")
    @Mapping(target = "status",
            expression = "java(returnUpdatedStatus(jobApplication, jobApplicationDtoUpdate, statusService))")
    @Mapping(target = "location", source = "jobApplicationDtoUpdate", qualifiedByName = "mapLocation")
    @Mapping(source = "jobApplicationDtoUpdate.hybrid", target = "hybrid")
    @Mapping(source = "jobApplicationDtoUpdate.skills", target = "qualifications", qualifiedByName = "mapSkills")
    JobApplication fromDtoIn(JobApplication jobApplication,
                             JobApplicationDtoUpdate jobApplicationDtoUpdate,
                             @Context StatusService statusService,
                             @Context LocationService locationService,
                             @Context SkillService skillService);

    @Mapping(source = "description", target = "description")
    @Mapping(source = "minSalary", target = "minSalary")
    @Mapping(source = "maxSalary", target = "maxSalary")
    @Mapping(source = "professional.firstName", target = "firstName")
    @Mapping(source = "professional.lastName", target = "lastName")
    @Mapping(source = "professional.email", target = "email")
    @Mapping(source = "jobApplication.location.name", target = "location")
    @Mapping(source = "hybrid", target = "hybrid")
    @Mapping(source = "jobApplication.status.status", target = "status")
    @Mapping(source = "qualifications", target = "skills", qualifiedByName = "setToString")
    @Mapping(target = "sentMatches", expression = "java(mapMatchedJobAds(jobApplication.getMatchesSentToJobAds()))")
    @Mapping(target = "requestedMatches", expression = "java(mapMatchedJobAds(jobApplication.getMatchRequestedAds()))")
    JobApplicationDtoOut toDtoOut(JobApplication jobApplication);

    List<JobApplicationDtoOut> toDtoOutList(List<JobApplication> jobApplication);

    Set<JobApplicationDtoOut> toDtoOutSet(Set<JobApplication> jobApplication);

    @Named("mapLocation")
    default Location mapLocation(JobApplicationDtoIn jobApplicationDtoIn, @Context LocationService locationService) {
        return locationService.returnIfExistOrCreate(jobApplicationDtoIn.getLocCountryIsoCode(),
                jobApplicationDtoIn.getLocCityId());
    }

    default Status returnUpdatedStatus(JobApplication jobApplication, JobApplicationDtoUpdate jobApplicationDtoUpdate,
                                       @Context StatusService statusService) {

        if (jobApplication.getStatus().getStatus().equals("Matched") &&
                !jobApplicationDtoUpdate.getStatus().equals("Matched")) {
            throw new EntityStatusException("Application", "Matched");
        }
        return statusService.getStatus(jobApplicationDtoUpdate.getStatus());
    }

    default Status returnInitialStatus(@Context StatusService statusService) {
        return statusService.getStatus("Active");
    }

    @Named("mapSkills")
    default Set<Skill> returnSetOfSkills(@Context SkillService skillService, String skillsStr) {
        if (skillsStr == null){
            return new HashSet<>();
        }

        String[] skillList = skillsStr.split(",");

        Set<Skill> skills = new HashSet<>();
        for (String s : skillList) {
            skills.add(skillService.getSkillByName(s));
        }
        return skills;
    }

    @Named("setToString")
    default String mapSetToString(Set<Skill> qualifications) {
        if (qualifications == null || qualifications.isEmpty()) {
            return "";
        }
        return qualifications.stream()
                .map(Skill::getName) // Convert Skill to its name
                .collect(Collectors.joining(", "));
    }

    default Set<JobAdDtoOut> mapMatchedJobAds(Set<JobAd> matchesSentToJobAds) {
        return (matchesSentToJobAds != null) ? JobAdMappers.INSTANCE.toDtoOutSet(matchesSentToJobAds) : new HashSet<>();
    }
}
