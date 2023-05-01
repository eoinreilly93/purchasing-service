package com.shop.generic.purchasingservice.rest.errorhandlers;

import com.shop.generic.common.rest.response.RestApiResponse;
import com.shop.generic.common.rest.response.RestApiResponseFactory;
import com.shop.generic.purchasingservice.exceptions.ServiceException;
import com.shop.generic.purchasingservice.exceptions.ServiceUnavailableException;
import com.shop.generic.purchasingservice.exceptions.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

//TODO: Move common exceptions to a global exception handler in common project
@ControllerAdvice
@Slf4j
public class ExceptionHandlerControllerAdvice {

    private final RestApiResponseFactory restApiResponseFactory;

    public ExceptionHandlerControllerAdvice(final RestApiResponseFactory restApiResponseFactory) {
        this.restApiResponseFactory = restApiResponseFactory;
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<RestApiResponse> handleValidationException(final ValidationException e) {
        log.error("Responding with bad request due to validation exception", e);
        return ResponseEntity.badRequest()
                .body(restApiResponseFactory.createErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<RestApiResponse> handleServiceUnavailableException(
            final ServiceUnavailableException e) {
        log.error("Responding with service unavailable error due to service being unavailable", e);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(restApiResponseFactory.createErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<RestApiResponse> handleServiceUnavailableException(
            final ServiceException e) {
        log.error("Responding with internal server error due to service being unavailable", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(restApiResponseFactory.createErrorResponse(e.getMessage()));
    }
}
