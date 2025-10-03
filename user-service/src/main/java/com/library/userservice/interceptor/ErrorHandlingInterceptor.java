package com.library.userservice.interceptor;

import com.library.userservice.exception.UserServiceException;
import io.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ErrorHandlingInterceptor implements ServerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(ErrorHandlingInterceptor.class);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {
        
        ServerCallHandler<ReqT, RespT> wrappedHandler = new ServerCallHandler<ReqT, RespT>() {
            @Override
            public ServerCall.Listener<ReqT> startCall(ServerCall<ReqT, RespT> call, Metadata headers) {
                try {
                    return next.startCall(call, headers);
                } catch (Exception e) {
                    handleException(call, e);
                    return new ServerCall.Listener<ReqT>() {};
                }
            }
        };
        
        return wrappedHandler.startCall(call, headers);
    }
    
    private <ReqT, RespT> void handleException(ServerCall<ReqT, RespT> call, Exception e) {
        String methodName = call.getMethodDescriptor().getFullMethodName();
        
        Status status;
        Metadata trailers = new Metadata();
        
        if (e instanceof UserServiceException) {
            UserServiceException use = (UserServiceException) e;
            status = mapToGrpcStatus(use);
            
            // Add custom error metadata
            trailers.put(Metadata.Key.of("error-code", Metadata.ASCII_STRING_MARSHALLER), 
                        use.getErrorCode().getCode());
            trailers.put(Metadata.Key.of("error-message", Metadata.ASCII_STRING_MARSHALLER), 
                        use.getMessage());
                        
            logger.warn("UserService exception in {}: code={}, message={}", 
                       methodName, use.getErrorCode().getCode(), use.getMessage());
        } else if (e instanceof IllegalArgumentException) {
            status = Status.INVALID_ARGUMENT.withDescription(e.getMessage());
            logger.warn("Invalid argument in {}: {}", methodName, e.getMessage());
        } else {
            status = Status.INTERNAL.withDescription("Internal server error");
            logger.error("Unexpected error in {}", methodName, e);
        }
        
        call.close(status, trailers);
    }
    
    private Status mapToGrpcStatus(UserServiceException e) {
        return switch (e.getErrorCode()) {
            case USER_NOT_FOUND -> Status.NOT_FOUND.withDescription(e.getMessage());
            case USER_ALREADY_EXISTS, EMAIL_ALREADY_EXISTS, USERNAME_ALREADY_EXISTS -> 
                Status.ALREADY_EXISTS.withDescription(e.getMessage());
            case USER_NOT_ACTIVE, USER_SUSPENDED, BORROWING_LIMIT_EXCEEDED, OUTSTANDING_FINES -> 
                Status.FAILED_PRECONDITION.withDescription(e.getMessage());
            case INVALID_USER_DATA, VALIDATION_ERROR -> Status.INVALID_ARGUMENT.withDescription(e.getMessage());
            case INVALID_CREDENTIALS -> Status.UNAUTHENTICATED.withDescription(e.getMessage());
            case DATABASE_ERROR -> Status.UNAVAILABLE.withDescription(e.getMessage());
            default -> Status.INTERNAL.withDescription(e.getMessage());
        };
    }
}