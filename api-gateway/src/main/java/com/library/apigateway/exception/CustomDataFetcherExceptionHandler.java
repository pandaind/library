package com.library.apigateway.exception;

import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.execution.DataFetcherExceptionHandler;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class CustomDataFetcherExceptionHandler implements DataFetcherExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(CustomDataFetcherExceptionHandler.class);

    @Override
    public CompletableFuture<DataFetcherExceptionHandlerResult> handleException(
            DataFetcherExceptionHandlerParameters handlerParameters) {
        
        Throwable exception = handlerParameters.getException();
        GraphQLError error = createGraphQLError(exception, handlerParameters);
        
        logger.error("GraphQL data fetcher exception: field={}, path={}", 
                    handlerParameters.getField().getName(), 
                    handlerParameters.getPath(), exception);
        
        DataFetcherExceptionHandlerResult result = DataFetcherExceptionHandlerResult.newResult()
                .error(error)
                .build();
                
        return CompletableFuture.completedFuture(result);
    }
    
    private GraphQLError createGraphQLError(Throwable exception, DataFetcherExceptionHandlerParameters params) {
        Map<String, Object> extensions = new HashMap<>();
        
        if (exception instanceof GraphQLException) {
            GraphQLException gqlEx = (GraphQLException) exception;
            extensions.put("errorCode", gqlEx.getErrorCode());
            extensions.put("timestamp", System.currentTimeMillis());
            
            return GraphqlErrorBuilder.newError()
                    .message(gqlEx.getMessage())
                    .errorType(gqlEx.getErrorType())
                    .path(params.getPath())
                    .location(params.getSourceLocation())
                    .extensions(extensions)
                    .build();
                    
        } else if (exception instanceof StatusRuntimeException) {
            StatusRuntimeException grpcEx = (StatusRuntimeException) exception;
            String errorCode = mapGrpcStatusToErrorCode(grpcEx.getStatus().getCode());
            
            extensions.put("errorCode", errorCode);
            extensions.put("grpcStatus", grpcEx.getStatus().getCode().name());
            extensions.put("timestamp", System.currentTimeMillis());
            
            return GraphqlErrorBuilder.newError()
                    .message(grpcEx.getStatus().getDescription() != null ? 
                            grpcEx.getStatus().getDescription() : "Service error occurred")
                    .errorType(mapGrpcStatusToErrorType(grpcEx.getStatus().getCode()))
                    .path(params.getPath())
                    .location(params.getSourceLocation())
                    .extensions(extensions)
                    .build();
                    
        } else if (exception instanceof ConstraintViolationException) {
            ConstraintViolationException validationEx = (ConstraintViolationException) exception;
            extensions.put("errorCode", "VALIDATION_ERROR");
            extensions.put("violations", validationEx.getConstraintViolations());
            extensions.put("timestamp", System.currentTimeMillis());
            
            return GraphqlErrorBuilder.newError()
                    .message("Input validation failed")
                    .errorType(ErrorType.ValidationError)
                    .path(params.getPath())
                    .location(params.getSourceLocation())
                    .extensions(extensions)
                    .build();
                    
        } else if (exception instanceof IllegalArgumentException) {
            extensions.put("errorCode", "INVALID_ARGUMENT");
            extensions.put("timestamp", System.currentTimeMillis());
            
            return GraphqlErrorBuilder.newError()
                    .message(exception.getMessage())
                    .errorType(ErrorType.ValidationError)
                    .path(params.getPath())
                    .location(params.getSourceLocation())
                    .extensions(extensions)
                    .build();
        }
        
        // Default error handling
        extensions.put("errorCode", "INTERNAL_ERROR");
        extensions.put("timestamp", System.currentTimeMillis());
        
        return GraphqlErrorBuilder.newError()
                .message("An unexpected error occurred")
                .errorType(ErrorType.DataFetchingException)
                .path(params.getPath())
                .location(params.getSourceLocation())
                .extensions(extensions)
                .build();
    }
    
    private String mapGrpcStatusToErrorCode(io.grpc.Status.Code statusCode) {
        return switch (statusCode) {
            case NOT_FOUND -> "RESOURCE_NOT_FOUND";
            case ALREADY_EXISTS -> "RESOURCE_ALREADY_EXISTS";
            case INVALID_ARGUMENT -> "INVALID_ARGUMENT";
            case FAILED_PRECONDITION -> "PRECONDITION_FAILED";
            case UNAUTHENTICATED -> "AUTHENTICATION_FAILED";
            case PERMISSION_DENIED -> "PERMISSION_DENIED";
            case UNAVAILABLE -> "SERVICE_UNAVAILABLE";
            case DEADLINE_EXCEEDED -> "TIMEOUT";
            default -> "INTERNAL_ERROR";
        };
    }
    
    private ErrorType mapGrpcStatusToErrorType(io.grpc.Status.Code statusCode) {
        return switch (statusCode) {
            case INVALID_ARGUMENT, FAILED_PRECONDITION -> ErrorType.ValidationError;
            case UNAUTHENTICATED, PERMISSION_DENIED -> ErrorType.ValidationError;
            case UNAVAILABLE, DEADLINE_EXCEEDED -> ErrorType.ExecutionAborted;
            default -> ErrorType.DataFetchingException;
        };
    }
}