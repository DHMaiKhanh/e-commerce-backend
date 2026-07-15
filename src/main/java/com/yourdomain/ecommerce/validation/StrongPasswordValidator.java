package com.yourdomain.ecommerce.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

    private int minLength;

    @Override
    public void initialize(StrongPassword constraintAnnotation) {
        this.minLength = constraintAnnotation.minLength();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.length() < minLength) {
            return false;
        }
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;
        for (char c : value.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if (!Character.isWhitespace(c)) hasSpecial = true;
        }
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }
}
