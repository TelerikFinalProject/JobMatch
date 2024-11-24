package com.telerikacademy.web.jobmatch.controllers;

import com.telerikacademy.web.jobmatch.services.contracts.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/locations")
public class LocationRestController {
    private final LocationService locationService;

    @Autowired
    public LocationRestController(LocationService locationService) {
        this.locationService = locationService;
    }
}
