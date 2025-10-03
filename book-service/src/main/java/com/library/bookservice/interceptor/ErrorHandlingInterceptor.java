package com.library.bookservice.interceptor;

import com.library.bookservice.exception.BookServiceException;
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
        
        if (e instanceof BookServiceException) {
            BookServiceException bse = (BookServiceException) e;
            status = mapToGrpcStatus(bse);
            
            // Add custom error metadata
            trailers.put(Metadata.Key.of("error-code", Metadata.ASCII_STRING_MARSHALLER), 
                        bse.getErrorCode().getCode());
            trailers.put(Metadata.Key.of("error-message", Metadata.ASCII_STRING_MARSHALLER), 
                        bse.getMessage());
                        
            logger.warn("BookService exception in {}: code={}, message={}", 
                       methodName, bse.getErrorCode().getCode(), bse.getMessage());
        } else if (e instanceof IllegalArgumentException) {
            status = Status.INVALID_ARGUMENT.withDescription(e.getMessage());
            logger.warn("Invalid argument in {}: {}", methodName, e.getMessage());
        } else {
            status = Status.INTERNAL.withDescription("Internal server error");
            logger.error("Unexpected error in {}", methodName, e);
        }
        
        call.close(status, trailers);
    }
    
    private Status mapToGrpcStatus(BookServiceException e) {
        return switch (e.getErrorCode()) {
            case BOOK_NOT_FOUND -> Status.NOT_FOUND.withDescription(e.getMessage());
            case BOOK_ALREADY_EXISTS -> Status.ALREADY_EXISTS.withDescription(e.getMessage());
            case BOOK_NOT_AVAILABLE, INSUFFICIENT_COPIES -> Status.FAILED_PRECONDITION.withDescription(e.getMessage());
            case INVALID_BOOK_DATA, VALIDATION_ERROR -> Status.INVALID_ARGUMENT.withDescription(e.getMessage());
            case DATABASE_ERROR -> Status.UNAVAILABLE.withDescription(e.getMessage());
            default -> Status.INTERNAL.withDescription(e.getMessage());
        };
    }
}