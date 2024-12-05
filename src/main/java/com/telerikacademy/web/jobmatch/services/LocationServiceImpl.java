package com.telerikacademy.web.jobmatch.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telerikacademy.web.jobmatch.exceptions.AuthorizationException;
import com.telerikacademy.web.jobmatch.exceptions.EntityDuplicateException;
import com.telerikacademy.web.jobmatch.exceptions.EntityNotFoundException;
import com.telerikacademy.web.jobmatch.exceptions.ExternalResourceException;
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
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LocationServiceImpl implements LocationService {

    public static final String EXTERNAL_API_KEY_VALUE = "SVVPWG5udjJUbktPZ1lOUExFeHJoZDlpUzJ6dHM0ZE9HZmo3Mk44Sw==";
    public static final String EXTERNAL_API_KEY_NAME = "X-CSCAPI-KEY";
    public static final String EXTERNAL_API_URL = "https://api.countrystatecity.in/v1/countries/";

    private final LocationRepository locationRepository;

    @Autowired
    public LocationServiceImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public List<Country> getAllCountries() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(EXTERNAL_API_URL))
                .header(EXTERNAL_API_KEY_NAME, EXTERNAL_API_KEY_VALUE)
                .build();

        try {
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            checkAccessToExternalApi(response);
            checkIfResourceExistsOnExternalApi(response);

            String responseBody = response.body();

            ObjectMapper objectMapper = new ObjectMapper();

            return objectMapper.readValue(responseBody, new TypeReference<>() {
            });
        } catch (IOException | InterruptedException e) {
            throw new ExternalResourceException(e.getMessage());
        }
    }

    @Override
    public Map<Integer, Location> getLocationsByCountry(String isoCode) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(EXTERNAL_API_URL + isoCode + "/cities"))
                .header(EXTERNAL_API_KEY_NAME, EXTERNAL_API_KEY_VALUE)
                .build();

        try {
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            checkAccessToExternalApi(response);
            checkIfResourceExistsOnExternalApi(response);

            ObjectMapper objectMapper = new ObjectMapper();

            List<Location> cities = objectMapper.readValue(response.body(), new TypeReference<>() {
            });

            if (cities.isEmpty()) {
                throw new EntityNotFoundException("Country", "Iso", isoCode);
            }

            return cities.stream()
                    .peek(city -> city.setIsoCode(isoCode))
                    .collect(Collectors.toMap(Location::getId, city -> city));
        } catch (IOException | InterruptedException e) {
            throw new ExternalResourceException(e.getMessage());
        }
    }

    @Override
    public Country getCountryByIsoCode(String isoCode) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(EXTERNAL_API_URL + isoCode))
                .header(EXTERNAL_API_KEY_NAME, EXTERNAL_API_KEY_VALUE)
                .build();

        try {
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            checkAccessToExternalApi(response);
            checkIfResourceExistsOnExternalApi(response);

            ObjectMapper objectMapper = new ObjectMapper();

            return objectMapper.readValue(response.body(), Country.class);
        } catch (IOException | InterruptedException e) {
            throw new ExternalResourceException(e.getMessage());
        }
    }

    @Override
    public List<Location> getAllSavedLocations(){
        return locationRepository.findAll();
    }

    @Override
    public Location returnIfExistOrCreate(String isoCode, int cityId) {
        Location location;
        boolean validLocation = true;
        try {
            location = getLocationById(cityId);
            if (!location.getIsoCode().equals(isoCode)) {
                validLocation = false;
            } else {
                return location;
            }
        } catch (EntityNotFoundException e) {
            try {
                Map<Integer, Location> locations = getLocationsByCountry(isoCode.toUpperCase());
                location = locations.get(cityId);
                if (location == null) {
                    validLocation = false;
                }
            } catch (EntityNotFoundException exception) {
                throw new EntityNotFoundException("Country", "Iso", isoCode);
            }
        }
        if (!validLocation) {
            try {
                Country country = getCountryByIsoCode(isoCode.toUpperCase());
                throw new EntityNotFoundException(String.format("City with ID:%d is not a city in %s.",
                        cityId, country.getName()));
            } catch (EntityNotFoundException exception) {
                throw new EntityNotFoundException("Country", "Iso", isoCode);
            }
        }
        addLocation(location, isoCode);
        return location;
    }

    @Override
    public Location getLocationById(int id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Location", id));
    }

    @Override
    public void addLocation(Location location, String countryIsoCode) {
        boolean exist = true;

        try {
            getLocationById(location.getId());
        } catch (EntityNotFoundException e) {
            exist = false;
        }

        if (exist) {
            throw new EntityDuplicateException("Location", "id", String.valueOf(location.getId()));
        }

        location.setIsoCode(countryIsoCode);
        locationRepository.save(location);
    }

    private void checkAccessToExternalApi(HttpResponse<String> response) {
        if (response.statusCode() == 401) {
            throw new AuthorizationException("access", "resource");
        }
    }

    private void checkIfResourceExistsOnExternalApi(HttpResponse<String> response) {
        if (response.statusCode() == 404) {
            throw new EntityNotFoundException("Resource cannot be found on external API");
        }
    }
}
