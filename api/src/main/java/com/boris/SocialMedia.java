package com.boris;

import com.boris.business.BusinessConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackageClasses = {SocialMedia.class, BusinessConfig.class})
public class SocialMedia {
    public static void main(String[] args) {
        SpringApplication.run(SocialMedia.class, args);
    }
}
