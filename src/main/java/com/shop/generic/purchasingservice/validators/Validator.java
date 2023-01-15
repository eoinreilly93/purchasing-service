package com.shop.generic.purchasingservice.validators;

import com.shop.generic.purchasingservice.exceptions.ValidationException;

public interface Validator<T> {

    void validate(T object) throws ValidationException;
}
