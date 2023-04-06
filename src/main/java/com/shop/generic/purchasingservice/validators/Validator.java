package com.shop.generic.purchasingservice.validators;

public interface Validator<T> {

    void validate(T object) throws Exception;
}
