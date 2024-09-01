package com.shop.generic.purchasingservice.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shop.generic.common.rest.response.RestApiResponse;
import com.shop.generic.purchasingservice.exceptions.ServiceException;
import com.shop.generic.purchasingservice.exceptions.ServiceUnavailableException;
import com.shop.generic.purchasingservice.exceptions.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

/**
 * This tests shows an example of mocking without any annotations
 * <p>
 * mock(RestTemplate.class) will create the mock at the instance level, ensuring it is available to
 * the RestTemplateUtil bean.
 * <p>
 * However, when you use @Mock annotations with @ExtendWith(MockitoExtension.class), mocks are only
 * initialized after the class is constructed, right before the test methods run, so that would fail
 * with this approach
 */
public class RestTemplateUtilTest {

    private final RestTemplate restTemplate = mock(RestTemplate.class);
    private final RestTemplateUtil restTemplateUtil = new RestTemplateUtil(restTemplate);

    @Test
    void testPostForObject_Success() throws Exception {
        // Given
        final String url = "http://example.com/api";
        final Object requestData = new Object();
        final RestApiResponse<String> expectedResponse = RestApiResponse.<String>builder()
                .result("Success")
                .build();

        final ResponseEntity<RestApiResponse<String>> responseEntity = new ResponseEntity<>(
                expectedResponse, HttpStatus.OK);
        final ParameterizedTypeReference<RestApiResponse<String>> typeReference = new ParameterizedTypeReference<>() {
        };

        when(restTemplate.exchange(eq(url), eq(HttpMethod.POST), any(HttpEntity.class),
                eq(typeReference)))
                .thenReturn(responseEntity);

        // When
        final RestApiResponse<String> actualResponse = restTemplateUtil.postForObject(url,
                requestData,
                typeReference);

        // Then
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getResult(), actualResponse.getResult());
        verify(restTemplate, times(1)).exchange(eq(url), eq(HttpMethod.POST), any(HttpEntity.class),
                eq(typeReference));
    }

    @Test
    void testPostForObject_ServiceUnavailableException() {
        // Given
        final String url = "http://example.com/api";
        final Object requestData = new Object();
        final ParameterizedTypeReference<RestApiResponse<String>> typeReference = new ParameterizedTypeReference<>() {
        };

        when(restTemplate.exchange(eq(url), eq(HttpMethod.POST), any(HttpEntity.class),
                eq(typeReference)))
                .thenThrow(new ResourceAccessException("Service unavailable"));

        // When & Then
        assertThrows(ServiceUnavailableException.class,
                () -> restTemplateUtil.postForObject(url, requestData, typeReference));
        verify(restTemplate, times(1)).exchange(eq(url), eq(HttpMethod.POST), any(HttpEntity.class),
                eq(typeReference));
    }

    @Test
    void testPostForObject_ValidationException() throws Exception {
        // Given
        final String url = "http://example.com/api";
        final Object requestData = new Object();
        final String errorMessage = "Validation failed";
        final ParameterizedTypeReference<RestApiResponse<String>> typeReference = new ParameterizedTypeReference<>() {
        };

        final HttpClientErrorException httpClientErrorException = HttpClientErrorException.create(
                HttpStatus.BAD_REQUEST, "Bad Request", HttpHeaders.EMPTY,
                ("{\"error\":\"" + errorMessage + "\"}").getBytes(), null);

        when(restTemplate.exchange(eq(url), eq(HttpMethod.POST), any(HttpEntity.class),
                eq(typeReference)))
                .thenThrow(httpClientErrorException);

        // When & Then
        final ValidationException exception = assertThrows(ValidationException.class, () ->
                restTemplateUtil.postForObject(url, requestData, typeReference));

        assertEquals(errorMessage, exception.getMessage());
        verify(restTemplate, times(1)).exchange(eq(url), eq(HttpMethod.POST), any(HttpEntity.class),
                eq(typeReference));
    }

    @Test
    void testPostForObject_ServiceException() throws Exception {
        // Given
        final String url = "http://example.com/api";
        final Object requestData = new Object();
        final String errorMessage = "Server error";
        final ParameterizedTypeReference<RestApiResponse<String>> typeReference = new ParameterizedTypeReference<>() {
        };

        final HttpClientErrorException httpClientErrorException = HttpClientErrorException.create(
                HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", HttpHeaders.EMPTY,
                ("{\"error\":\"" + errorMessage + "\"}").getBytes(), null);

        when(restTemplate.exchange(eq(url), eq(HttpMethod.POST), any(HttpEntity.class),
                eq(typeReference)))
                .thenThrow(httpClientErrorException);

        // When & Then
        final ServiceException exception = assertThrows(ServiceException.class, () ->
                restTemplateUtil.postForObject(url, requestData, typeReference));

        assertEquals(errorMessage, exception.getMessage());
        verify(restTemplate, times(1)).exchange(eq(url), eq(HttpMethod.POST), any(HttpEntity.class),
                eq(typeReference));
    }

    @Test
    void testGetForObject_Success() throws Exception {
        // Given
        final String url = "http://example.com/api";
        final RestApiResponse<String> expectedResponse = RestApiResponse.<String>builder()
                .result("Success")
                .build();

        final ResponseEntity<RestApiResponse<String>> responseEntity = new ResponseEntity<>(
                expectedResponse, HttpStatus.OK);
        final ParameterizedTypeReference<RestApiResponse<String>> typeReference = new ParameterizedTypeReference<>() {
        };

        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), isNull(), eq(typeReference)))
                .thenReturn(responseEntity);

        // When
        final RestApiResponse<String> actualResponse = restTemplateUtil.getForObject(url,
                typeReference);

        // Then
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getResult(), actualResponse.getResult());
        verify(restTemplate, times(1)).exchange(eq(url), eq(HttpMethod.GET), isNull(),
                eq(typeReference));
    }

    @Test
    void testPutForObject_Success() throws Exception {
        // Given
        final String url = "http://example.com/api";
        final Object requestData = new Object();
        final RestApiResponse<String> expectedResponse = RestApiResponse.<String>builder()
                .result("Success")
                .build();

        final ResponseEntity<RestApiResponse<String>> responseEntity = new ResponseEntity<>(
                expectedResponse, HttpStatus.OK);
        final ParameterizedTypeReference<RestApiResponse<String>> typeReference = new ParameterizedTypeReference<>() {
        };

        when(restTemplate.exchange(eq(url), eq(HttpMethod.PUT), any(HttpEntity.class),
                eq(typeReference)))
                .thenReturn(responseEntity);

        // When
        final RestApiResponse<String> actualResponse = restTemplateUtil.putForObject(url,
                requestData,
                typeReference);

        // Then
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getResult(), actualResponse.getResult());
        verify(restTemplate, times(1)).exchange(eq(url), eq(HttpMethod.PUT), any(HttpEntity.class),
                eq(typeReference));
    }
}