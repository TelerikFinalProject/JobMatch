package com.telerikacademy.web.jobmatch.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telerikacademy.web.jobmatch.exceptions.EntityDuplicateException;
import com.telerikacademy.web.jobmatch.exceptions.EntityNotFoundException;
import com.telerikacademy.web.jobmatch.models.Country;
import com.telerikacademy.web.jobmatch.models.Location;
import com.telerikacademy.web.jobmatch.repositories.contracts.LocationRepository;
import com.telerikacademy.web.jobmatch.services.contracts.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
public class LocationServiceImpl implements LocationService {

    public static final String EXTERNAL_API_KEY_VALUE = "SVVPWG5udjJUbktPZ1lOUExFeHJoZDlpUzJ6dHM0ZE9HZmo3Mk44Sw==";
    public static final String EXTERNAL_API_KEY_NAME = "X-CSCAPI-KEY";
    public static final String EXTERNAL_API_URL = "https://api.countrystatecity.in/v1/countries";

    private final LocationRepository locationRepository;

    @Autowired
    public LocationServiceImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public List<Country> getAllCountries() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(EXTERNAL_API_URL))
                .header(EXTERNAL_API_KEY_NAME, EXTERNAL_API_KEY_VALUE)
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        String responseBody = response.body();

        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(responseBody, new TypeReference<>() {});
    }

    @Override
    public List<Location> getLocationsByCountry(String isoCode) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(EXTERNAL_API_URL + isoCode +"/cities"))
                .header(EXTERNAL_API_KEY_NAME, EXTERNAL_API_KEY_VALUE)
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(response.body(), new TypeReference<>() {});
    }

    @Override
    public Country getCountryByIsoCode(String isoCode) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(EXTERNAL_API_URL + isoCode))
                .header(EXTERNAL_API_KEY_NAME, EXTERNAL_API_KEY_VALUE)
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(response.body(), Country.class);
    }

    @Override
    public Location getLocationById(int id) {
        return locationRepository.getLocation(id);
    }

    @Override
    public void addLocation(Location location, Country country) {
        boolean exist = true;

        try {
            locationRepository.getLocation(location.getId());
        } catch (EntityNotFoundException e) {
            exist = false;
        }

        if (exist) {
            throw new EntityDuplicateException("Location", "id", String.valueOf(location.getId()));
        }

        location.setIsoCode(location.getIsoCode());
        locationRepository.addLocation(location);
    }
}
