package com.gousade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = "com.gousade")
public class ChiralCarbonCaptchaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChiralCarbonCaptchaApplication.class, args);
    }

}
