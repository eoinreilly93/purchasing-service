package com.shop.purchasingservice.rest.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class RestApiResponse<T> {

    private final String message;
    private final String error;
    private final T result;
    private final LocalDateTime timestamp;

}
