package com.imooc.pan.web.validator;

import com.imooc.pan.core.constants.RPanConstants;
import lombok.extern.log4j.Log4j2;
import org.hibernate.validator.HibernateValidator;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;


/**
 * 统一的参数校验器
 */
@SpringBootConfiguration
@Log4j2
public class WebValidatorConfig {

    private static final String FAIL_FAST_KEY = "hibernate.validator.fail_fast";

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {

        MethodValidationPostProcessor postProcessor = new MethodValidationPostProcessor();
        postProcessor.setValidator(rPanValidator());
        log.info("The hibernate validator is loaded successfully!");
        return postProcessor;
    }

    private Validator rPanValidator() {
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
                .configure()
                .addProperty(FAIL_FAST_KEY, RPanConstants.TRUE_STR)
                .buildValidatorFactory();

        Validator validator = validatorFactory.getValidator();
        return validator;
    }

}
