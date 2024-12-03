package com.telerikacademy.web.jobmatch.services;

import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.ClientOptions;
import com.mailjet.client.resource.Emailv31;
import com.telerikacademy.web.jobmatch.models.JobAd;
import com.telerikacademy.web.jobmatch.models.JobApplication;
import com.telerikacademy.web.jobmatch.models.Professional;
import com.telerikacademy.web.jobmatch.services.contracts.MailService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class MailServiceImpl implements MailService {

    public static final String SUBJECT_MATCH_REQ_RECEIVED = "Match request has been received!";
    public static final String PUBLIC_API_KEY = "56017ea9f1318251ace26bd33723cbef";
    public static final String PRIVATE_API_KEY = "a80ae6f8fc1c57a3bd4bfb490a027489";
    public static final String STAFF_MAIL = "simonankov1@gmail.com";
    public static final String HTML_MESSAGE = "src/main/resources/templates/mail-notification.html";

    @Override
    public void sendNotificationViaEmailToEmployer(JobApplication jobApplication, JobAd jobAd) {
        String recipient = jobAd.getEmployer().getCompanyName();

        Professional professional = jobApplication.getProfessional();
        String requester = professional.getFirstName() + " " + professional.getLastName();

        String jobTitle = jobAd.getPositionTitle();

        emailRequestNotificationTemplate(recipient, requester, jobTitle);
    }

    @Override
    public void sendNotificationViaEmailToApplicant(JobApplication jobApplication, JobAd jobAd) {
        Professional professional = jobApplication.getProfessional();
        String recipient = professional.getFirstName() + " " + professional.getLastName();

        String requester = jobAd.getEmployer().getCompanyName();
        String jobTitle = jobAd.getPositionTitle();

        emailRequestNotificationTemplate(recipient, requester, jobTitle);
    }

    private void emailRequestNotificationTemplate(String recipient, String requester, String jobTitle){
        try {
            MailjetClient client;
            MailjetRequest request;
            MailjetResponse response;

            // Building the client options
            ClientOptions options = ClientOptions.builder()
                    .apiKey(PUBLIC_API_KEY)   // Set API key
                    .apiSecretKey(PRIVATE_API_KEY)  // Set secret key
                    .build();

            String htmlTemplate = new String(Files.readAllBytes(Paths.get(HTML_MESSAGE)));

            String personalizedHtml = htmlTemplate.replace("{{recipient}}", recipient).replace("{{requester}}", requester)
                    .replace("{{jobAdTitle}}", jobTitle);

            client = new MailjetClient(options);
            request = new MailjetRequest(Emailv31.resource)
                    .property(Emailv31.MESSAGES, new JSONArray()
                            .put(new JSONObject()
                                    .put(Emailv31.Message.FROM, new JSONObject()
                                            .put("Email", STAFF_MAIL)
                                            .put("Name", "Job Match"))
                                    .put(Emailv31.Message.TO, new JSONArray()
                                            .put(new JSONObject()
                                                    .put("Email", "jobmatchapplication@gmail.com")
                                                    .put("Name", recipient)))
                                    .put(Emailv31.Message.SUBJECT, SUBJECT_MATCH_REQ_RECEIVED)
                                    .put(Emailv31.Message.HTMLPART, personalizedHtml)));
            response = client.post(request);
            System.out.println(response.getStatus());
            System.out.println(response.getData());
        } catch (MailjetException |  IOException ex){
            System.err.println("An error occurred while sending the email: " + ex.getMessage());
        }
    }
}