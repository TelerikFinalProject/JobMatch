package com.telerikacademy.web.jobmatch.services.contracts;

import com.telerikacademy.web.jobmatch.models.Country;
import com.telerikacademy.web.jobmatch.models.Location;

import java.io.IOException;
import java.util.List;

public interface LocationService {
    List<Country> getAllCountries() throws IOException, InterruptedException;

    List<Location> getLocationsByCountry(String isoCode) throws IOException, InterruptedException;

    Country getCountryByIsoCode(String isoCode) throws IOException, InterruptedException;

    Location getLocationById(int id);

    void addLocation(Location location, Country country);
}
