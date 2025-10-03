package com.library.apigateway.exception;

import graphql.ErrorType;
import graphql.GraphqlErrorBuilder;
import graphql.execution.DataFetcherExceptionHandler;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;

public class GraphQLException extends RuntimeException {
    private final ErrorType errorType;
    private final String errorCode;
    
    public GraphQLException(String message) {
        super(message);
        this.errorType = ErrorType.DataFetchingException;
        this.errorCode = "INTERNAL_ERROR";
    }
    
    public GraphQLException(String message, ErrorType errorType) {
        super(message);
        this.errorType = errorType;
        this.errorCode = "INTERNAL_ERROR";
    }
    
    public GraphQLException(String message, String errorCode) {
        super(message);
        this.errorType = ErrorType.DataFetchingException;
        this.errorCode = errorCode;
    }
    
    public GraphQLException(String message, ErrorType errorType, String errorCode) {
        super(message);
        this.errorType = errorType;
        this.errorCode = errorCode;
    }
    
    public ErrorType getErrorType() {
        return errorType;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}