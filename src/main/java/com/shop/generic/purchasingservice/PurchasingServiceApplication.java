package com.shop.generic.purchasingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.shop.generic")
public class PurchasingServiceApplication {

    public static void main(final String[] args) {
        SpringApplication.run(PurchasingServiceApplication.class, args);
    }
}
