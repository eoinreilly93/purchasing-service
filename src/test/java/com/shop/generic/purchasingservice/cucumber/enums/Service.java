package com.shop.generic.purchasingservice.cucumber.enums;

import lombok.Getter;

@Getter
public enum Service {

    ORDERS("/order-service"),
    PRODUCTS("/product-service");

    private final String contextPath;

    Service(final String contextPath) {
        this.contextPath = contextPath;
    }
}
