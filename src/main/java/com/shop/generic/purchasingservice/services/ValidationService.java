package com.shop.generic.purchasingservice.services;

import com.shop.generic.common.valueobjects.PurchaseProductVO;
import com.shop.generic.purchasingservice.exceptions.ValidationException;
import com.shop.generic.purchasingservice.validators.Validator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ValidationService {

    private final List<Validator> validatorList;

    public ValidationService(final List<Validator> validatorList) {
        this.validatorList = validatorList;
    }

    public boolean validate(final List<PurchaseProductVO> purchaseProductVO)
            throws ValidationException {
        for (final Validator validator : validatorList) {
            log.info("Validating purchase against {}", validator.getClass().getSimpleName());
            validator.validate(purchaseProductVO);
        }
        return true;
    }

}
