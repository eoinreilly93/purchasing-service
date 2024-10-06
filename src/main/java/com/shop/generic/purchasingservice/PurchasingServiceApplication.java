package com.shop.generic.purchasingservice;

import com.shop.generic.common.CommonKafkaConsumerAutoConfiguration;
import com.shop.generic.common.CommonKafkaProducerAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {CommonKafkaConsumerAutoConfiguration.class,
        CommonKafkaProducerAutoConfiguration.class})
public class PurchasingServiceApplication {

    public static void main(final String[] args) {
        SpringApplication.run(PurchasingServiceApplication.class, args);
    }
}
