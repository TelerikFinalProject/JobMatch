package com.telerikacademy.web.jobmatch.helpers;

import com.telerikacademy.web.jobmatch.models.JobAd;
import com.telerikacademy.web.jobmatch.models.Location;
import com.telerikacademy.web.jobmatch.models.Skill;
import com.telerikacademy.web.jobmatch.models.Status;
import com.telerikacademy.web.jobmatch.models.dtos.JobAdDtoIn;
import com.telerikacademy.web.jobmatch.models.dtos.JobAdDtoOut;
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
public interface JobAdMappers {
    JobAdMappers INSTANCE = Mappers.getMapper(JobAdMappers.class);


    @Mapping(source = "positionTitle", target = "positionTitle")
    @Mapping(source = "minSalary", target = "minSalary")
    @Mapping(source = "maxSalary", target = "maxSalary")
    @Mapping(source = "jobDescription", target = "jobDescription")
    @Mapping(source = "hybrid", target = "hybrid")
    @Mapping(target = "location", source = "jobAdDtoIn", qualifiedByName = "mapLocation")
    @Mapping(target = "status", expression = "java(returnInitialStatus(statusService))")
    @Mapping(source = "skills", target = "skills", qualifiedByName = "mapSkills")
    JobAd fromDtoIn(JobAdDtoIn jobAdDtoIn,
                    @Context LocationService locationService,
                    @Context StatusService statusService,
                    @Context SkillService skillService);

    @Mapping(source = "jobAd.id", target = "id")
    @Mapping(source = "jobAdDtoIn.positionTitle", target = "positionTitle")
    @Mapping(source = "jobAdDtoIn.minSalary", target = "minSalary")
    @Mapping(source = "jobAdDtoIn.maxSalary", target = "maxSalary")
    @Mapping(source = "jobAdDtoIn.jobDescription", target = "jobDescription")
    @Mapping(source = "jobAdDtoIn.hybrid", target = "hybrid")
    @Mapping(target = "location", source = "jobAdDtoIn", qualifiedByName = "mapLocation")
    @Mapping(target = "status", expression = "java(returnInitialStatus(statusService))")
    @Mapping(source = "jobAdDtoIn.skills", target = "skills", qualifiedByName = "mapSkills")
    JobAd fromDtoIn(JobAd jobAd,
                    JobAdDtoIn jobAdDtoIn,
                    @Context LocationService locationService,
                    @Context StatusService statusService,
                    @Context SkillService skillService);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "positionTitle", target = "positionTitle")
    @Mapping(source = "minSalary", target = "minSalary")
    @Mapping(source = "maxSalary", target = "maxSalary")
    @Mapping(source = "jobDescription", target = "jobDescription")
    @Mapping(target = "city", source = "jobAd.location.name") // Nested mapping for role
    @Mapping(source = "jobAd.employer.companyName", target = "companyName")// Nested mapping for location
    @Mapping(target = "hybrid", source = "hybrid")
    @Mapping(source = "skills", target = "skills", qualifiedByName = "setToString")
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

    @Named("mapSkills")
    default Set<Skill> returnSetOfSkills(@Context SkillService skillService, String skillsStr) {
        String[] skillList = skillsStr.split(", ");

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

}
