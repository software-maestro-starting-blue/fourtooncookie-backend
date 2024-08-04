package com.startingblue.fourtooncookie.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class NotEmptyListValidator implements ConstraintValidator<NotEmptyList, List<?>> {

    @Override
    public boolean isValid(List<?> value, ConstraintValidatorContext context) {
        return value != null && !value.isEmpty();
    }
}
