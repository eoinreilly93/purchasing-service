package com.shop.generic.purchasingservice.configurations;

import com.shop.generic.purchasingservice.validators.PaymentValidator;
import com.shop.generic.purchasingservice.validators.ProductsCanBePurchasedValidator;
import com.shop.generic.purchasingservice.validators.ProductsExistValidator;
import com.shop.generic.purchasingservice.validators.Validator;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValidatorConfiguration {

    @Autowired
    private ProductsExistValidator productsExistValidator;

    @Autowired
    private ProductsCanBePurchasedValidator productsCanBePurchasedValidator;

    @Autowired
    private PaymentValidator paymentValidator;

    @Bean
    public List<Validator<?>> validatorList() {
        final List<Validator<?>> validatorList = new ArrayList<>();
        validatorList.add(productsExistValidator);
        validatorList.add(productsCanBePurchasedValidator);
        validatorList.add(paymentValidator);
        return validatorList;
    }
}
