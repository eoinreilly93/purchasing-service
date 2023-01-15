package com.shop.generic.purchasingservice.configurations;

import com.shop.generic.purchasingservice.rest.interceptors.MicroserviceRestTemplateInterceptor;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfiguration {

    private final MicroserviceRestTemplateInterceptor microserviceRestTemplateInterceptor;

    public RestTemplateConfiguration(
            final MicroserviceRestTemplateInterceptor microserviceRestTemplateInterceptor) {
        this.microserviceRestTemplateInterceptor = microserviceRestTemplateInterceptor;
    }

    @Bean
    public RestTemplate restTemplate() {
        final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setReadTimeout(120000);
        requestFactory.setConnectTimeout(60000);
        final RestTemplate restTemplate = new RestTemplate(requestFactory);
        restTemplate.setInterceptors(List.of(microserviceRestTemplateInterceptor));
        return restTemplate;
    }
}
