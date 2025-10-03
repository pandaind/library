package com.library.apigateway.exception;

import graphql.ErrorType;

public class ValidationException extends GraphQLException {
    public ValidationException(String message) {
        super(message, ErrorType.ValidationError, "VALIDATION_ERROR");
    }
    
    public ValidationException(String field, String reason) {
        super("Validation failed for field '" + field + "': " + reason, ErrorType.ValidationError, "VALIDATION_ERROR");
    }
}