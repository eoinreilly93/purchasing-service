package com.shop.generic.purchasingservice.exceptions;

public class ServiceUnavailableException extends RuntimeException {

    public ServiceUnavailableException(final String message) {
        super(message);
    }

}
