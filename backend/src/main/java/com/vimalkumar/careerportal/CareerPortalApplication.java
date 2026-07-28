package com.vimalkumar.careerportal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CareerPortalApplication {
    public static void main(String[] args) {
        SpringApplication.run(CareerPortalApplication.class, args);
    }
}
