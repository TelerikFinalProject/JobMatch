package com.telerikacademy.web.jobmatch.helpers;

import com.telerikacademy.web.jobmatch.models.Location;
import com.telerikacademy.web.jobmatch.models.UserPrincipal;
import com.telerikacademy.web.jobmatch.models.dtos.UserDtoIn;
import com.telerikacademy.web.jobmatch.services.contracts.LocationService;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AdminMappers {
    AdminMappers INSTANCE = Mappers.getMapper(AdminMappers.class);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "email", target = "email")
    @Mapping(target = "location", source = "userDtoIn", qualifiedByName = "mapLocation")
    @Mapping(target = "roles", expression = "java(returnInitialRole())")
    UserPrincipal fromDtoIn(UserDtoIn userDtoIn,
                            @Context LocationService locationService);

    @Named("mapLocation")
    default Location mapLocation(UserDtoIn userDtoIn, @Context LocationService locationService) {
        return locationService.returnIfExistOrCreate(userDtoIn.getLocCountryIsoCode(),
                userDtoIn.getLocCityId());
    }

    default String returnInitialRole(){
        return "ADMIN";
    }
}
