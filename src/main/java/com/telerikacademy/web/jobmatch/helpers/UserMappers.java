package com.telerikacademy.web.jobmatch.helpers;

import com.telerikacademy.web.jobmatch.models.Location;
import com.telerikacademy.web.jobmatch.models.UserPrincipal;
import com.telerikacademy.web.jobmatch.models.dtos.users.EmployerDtoIn;
import com.telerikacademy.web.jobmatch.models.dtos.users.UserDtoIn;
import com.telerikacademy.web.jobmatch.services.contracts.LocationService;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMappers {
    UserMappers INSTANCE = Mappers.getMapper(UserMappers.class);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "password", target = "password")
    @Mapping(target = "location", source = "userDtoIn", qualifiedByName = "mapLocation")
    UserPrincipal fromDtoIn(UserDtoIn userDtoIn,
                            @Context LocationService locationService);

    @Named("mapLocation")
    default Location mapLocation(UserDtoIn userDtoIn, @Context LocationService locationService) {
        return locationService.returnIfExistOrCreate(userDtoIn.getLocCountryIsoCode(),
                userDtoIn.getLocCityId());
    }
}
