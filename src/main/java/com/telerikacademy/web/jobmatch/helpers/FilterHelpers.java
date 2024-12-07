package com.telerikacademy.web.jobmatch.helpers;

import com.telerikacademy.web.jobmatch.models.dtos.filters.JobAdFilterDto;
import com.telerikacademy.web.jobmatch.models.dtos.filters.JobApplicationFilterDto;

public class FilterHelpers {
    public static void jobAdMvcFilterHelper(JobAdFilterDto filter) {
        filter.setCreator(checkForEmptyStrings(filter.getCreator()));
        filter.setLocation(checkForEmptyStrings(filter.getLocation()));
        filter.setHybrid(hybridChecker(filter.getHybrid()));
    }

    public static void jobApplicationMvcFilterHelper(JobApplicationFilterDto filter) {
        filter.setLocation(checkForEmptyStrings(filter.getLocation()));
        filter.setHybrid(hybridChecker(filter.getHybrid()));
    }

    private static Boolean hybridChecker(Boolean isHybrid) {
        if (isHybrid != null && !isHybrid) {
            return null;
        }
        return isHybrid;
    }

    private static String checkForEmptyStrings(String string){
        if (string == null || string.isEmpty()){
            return null;
        }
        return string;
    }
}
