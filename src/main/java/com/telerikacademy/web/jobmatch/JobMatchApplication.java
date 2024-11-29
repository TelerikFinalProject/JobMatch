package com.telerikacademy.web.jobmatch;

import com.telerikacademy.web.jobmatch.records.RSAKeyRecord;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(RSAKeyRecord.class)
@SpringBootApplication
public class JobMatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobMatchApplication.class, args);
	}

}
