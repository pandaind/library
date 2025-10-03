package com.library.apigateway.exception;

import graphql.ErrorType;

public class ServiceUnavailableException extends GraphQLException {
    public ServiceUnavailableException(String serviceName) {
        super("Service unavailable: " + serviceName, ErrorType.ExecutionAborted, "SERVICE_UNAVAILABLE");
    }
    
    public ServiceUnavailableException(String serviceName, Throwable cause) {
        super("Service unavailable: " + serviceName + ". Reason: " + cause.getMessage(), ErrorType.ExecutionAborted, "SERVICE_UNAVAILABLE");
        initCause(cause);
    }
}