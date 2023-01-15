package com.shop.generic.purchasingservice.rest.interceptors;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MicroserviceRestTemplateInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(final HttpRequest request, final byte[] body,
            final ClientHttpRequestExecution execution) throws IOException {
        final long start = System.currentTimeMillis();
        log.info("Sending {} request to microservice {} with data {}", request.getMethod(),
                request.getURI(), new String(body));
        //TODO: Populate any additional headers here such as bearerTokens

        final ClientHttpResponse response = execution.execute(request, body);
        final long finish = System.currentTimeMillis();
        log.info("Response received with {} status in {}ms", response.getStatusCode(),
                finish - start);
        return response;
    }
}
