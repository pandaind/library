package com.library.apigateway.validation;

import com.library.apigateway.exception.ValidationException;
import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class InputValidator {
    
    private final Validator validator;
    
    public InputValidator(Validator validator) {
        this.validator = validator;
    }
    
    public <T> void validate(T input) {
        Set<ConstraintViolation<T>> violations = validator.validate(input);
        
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                    .collect(Collectors.joining(", "));
                    
            throw new ValidationException("Validation failed: " + message);
        }
    }
    
    public <T> void validateProperty(T input, String propertyName) {
        Set<ConstraintViolation<T>> violations = validator.validateProperty(input, propertyName);
        
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
                    
            throw new ValidationException("Validation failed for " + propertyName + ": " + message);
        }
    }
}