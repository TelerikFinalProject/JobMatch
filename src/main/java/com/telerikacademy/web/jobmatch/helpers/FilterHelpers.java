package com.telerikacademy.web.jobmatch.helpers;

import com.telerikacademy.web.jobmatch.models.dtos.filters.JobAdFilterDto;
import com.telerikacademy.web.jobmatch.models.dtos.filters.JobApplicationFilterDto;

public class FilterHelpers {
    public static void jobAdMvcFilterHelper(JobAdFilterDto filter) {
        filter.setCreator(checkForEmptyStrings(filter.getCreator()));
        filter.setLocation(checkForEmptyStrings(filter.getLocation()));
        filter.setHybrid(hybridChecker(filter.getHybrid()));
        filter.setStatus(checkStatus(filter.getStatus()));
    }

    public static void jobApplicationMvcFilterHelper(JobApplicationFilterDto filter) {
        filter.setLocation(checkForEmptyStrings(filter.getLocation()));
        filter.setHybrid(hybridChecker(filter.getHybrid()));
        filter.setStatus(checkStatus(filter.getStatus()));
    }

    private static Boolean hybridChecker(Boolean isHybrid) {
        if (isHybrid == null || !isHybrid) {
            return null;
        }
        return Boolean.TRUE;
    }

    private static String checkStatus(String status) {
        if (status == null || status.isEmpty()){
            return "Active";
        }
        return status;
    }

    private static String checkForEmptyStrings(String string){
        if (string == null || string.isEmpty()){
            return null;
        }
        return string;
    }
}
