package com.shop.generic.purchasingservice.services;

import com.shop.generic.purchasingservice.configurations.ValidatorConfiguration;
import com.shop.generic.purchasingservice.exceptions.ValidationException;
import com.shop.generic.purchasingservice.models.EnrichedPurchaseRequest;
import com.shop.generic.purchasingservice.validators.Validator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ValidationService {

    private final List<Validator<?>> validatorList;

    /**
     * @param validatorList the list of validators configured in {@link ValidatorConfiguration} The
     *                      qualifier annotation is required to guarantee the bean with the ordered
     *                      list is returned, otherwise it can return them in any order
     */
    public ValidationService(@Qualifier("validatorList") final List<Validator<?>> validatorList) {
        this.validatorList = validatorList;
    }

    public boolean validate(final EnrichedPurchaseRequest enrichedPurchaseRequest)
            throws ValidationException {
        for (final Validator validator : validatorList) {
            log.info("Validating purchase against {}", validator.getClass().getSimpleName());
            validator.validate(enrichedPurchaseRequest);
        }
        return true;
    }

}
