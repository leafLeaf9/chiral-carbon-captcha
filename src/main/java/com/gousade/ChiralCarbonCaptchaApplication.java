package com.gousade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.gousade", "direction"})
public class ChiralCarbonCaptchaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChiralCarbonCaptchaApplication.class, args);
    }

}
