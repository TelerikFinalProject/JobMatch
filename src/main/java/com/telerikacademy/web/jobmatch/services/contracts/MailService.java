package com.telerikacademy.web.jobmatch.services.contracts;

import com.mailjet.client.errors.MailjetException;
import com.telerikacademy.web.jobmatch.models.Employer;
import com.telerikacademy.web.jobmatch.models.JobAd;
import com.telerikacademy.web.jobmatch.models.JobApplication;
import com.telerikacademy.web.jobmatch.models.Professional;

import java.io.IOException;

public interface MailService {
    void sendNotificationViaEmailToEmployer(JobApplication jobApplication, JobAd jobAd);

    void sendNotificationViaEmailToApplicant(JobApplication jobApplication, JobAd jobAd);
}
