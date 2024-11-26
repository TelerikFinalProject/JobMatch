package com.telerikacademy.web.jobmatch.services.contracts;

import com.telerikacademy.web.jobmatch.models.Country;
import com.telerikacademy.web.jobmatch.models.Location;

import java.util.List;
import java.util.Map;

public interface LocationService {
    List<Country> getAllCountries();

    Map<Integer, Location> getLocationsByCountry(String isoCode);

    Country getCountryByIsoCode(String isoCode);

    Location returnIfExistOrCreate(String isoCode, int cityId);

    Location getLocationById(int id);

    void addLocation(Location location, String countryIsoCode);
}
