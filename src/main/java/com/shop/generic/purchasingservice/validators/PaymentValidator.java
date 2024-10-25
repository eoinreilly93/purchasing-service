package com.shop.generic.purchasingservice.validators;

import com.shop.generic.common.rest.exceptions.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PaymentValidator implements Validator {

    @Override
    public void validate(final Object object) throws ValidationException {
        log.info("Validating payment...");
    }
}
