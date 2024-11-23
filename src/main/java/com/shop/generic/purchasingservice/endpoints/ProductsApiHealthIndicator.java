package com.shop.generic.purchasingservice.endpoints;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class ProductsApiHealthIndicator implements HealthIndicator {

    private static final String HTTP_API_DETAIL_KEY = "url";
    private static final String HTTP_STATUS_DETAIL_KEY = "HTTP Response Status";
    private static final String HTTP_ERROR_DETAIL_KEY = "HTTP Response Exception";

    @Value("${services.product-service.url}")
    private String productServiceBaseUrl;

    private final RestTemplate restTemplate;

    public ProductsApiHealthIndicator(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Health health() {
        final Health.Builder builder = new Health.Builder().withDetail(HTTP_API_DETAIL_KEY,
                productServiceBaseUrl);
        try {
            final HttpStatusCode status = checkApiConnectivity();
            if (status.is2xxSuccessful()) {
                return builder.status(Status.UP).withDetail(HTTP_STATUS_DETAIL_KEY, status).build();
            } else {
                return builder.status(Status.OUT_OF_SERVICE)
                        .withDetail(HTTP_ERROR_DETAIL_KEY, status).build();
            }
        } catch (final HttpClientErrorException e) {
            return builder.status(Status.OUT_OF_SERVICE)
                    .withDetail(HTTP_STATUS_DETAIL_KEY, e.getStatusCode())
                    .withDetail(HTTP_ERROR_DETAIL_KEY, e.getLocalizedMessage())
                    .build();
        } catch (final Exception e) {
            return builder.status(Status.OUT_OF_SERVICE)
                    .withDetail(HTTP_STATUS_DETAIL_KEY, HttpStatus.SERVICE_UNAVAILABLE)
                    .withDetail(HTTP_ERROR_DETAIL_KEY, e.getMessage())
                    .build();
        }
    }

    private HttpStatusCode checkApiConnectivity() {
        try {
            final ResponseEntity response = this.restTemplate.getForEntity(
                    productServiceBaseUrl + "/actuator/health",
                    String.class);
            return response.getStatusCode();
        } catch (final HttpClientErrorException e) {
            return e.getStatusCode();
        } catch (final Exception e) {
            return HttpStatus.NOT_FOUND;
        }
    }
}
