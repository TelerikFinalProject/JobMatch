package com.telerikacademy.web.jobmatch.repositories.contracts;

import com.telerikacademy.web.jobmatch.models.Location;

public interface LocationRepository {
    Location getLocation(int id);

    void addLocation(Location location);
}
