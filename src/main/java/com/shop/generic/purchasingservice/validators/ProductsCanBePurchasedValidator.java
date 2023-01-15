package com.shop.generic.purchasingservice.validators;

import com.shop.generic.common.valueobjects.PurchaseProductVO;
import com.shop.generic.purchasingservice.exceptions.ValidationException;
import com.shop.generic.purchasingservice.util.RestTemplateUtil;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ProductsCanBePurchasedValidator implements Validator<List<PurchaseProductVO>> {

    private final RestTemplateUtil restTemplateUtil;

    public ProductsCanBePurchasedValidator(final RestTemplateUtil restTemplateUtil) {
        this.restTemplateUtil = restTemplateUtil;
    }

    @Override
    public void validate(final List<PurchaseProductVO> object) throws ValidationException {

    }
}
