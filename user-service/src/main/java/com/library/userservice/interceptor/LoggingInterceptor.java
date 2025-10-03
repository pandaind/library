package com.library.userservice.interceptor;

import io.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingInterceptor implements ServerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {
        
        String methodName = call.getMethodDescriptor().getFullMethodName();
        String clientAddress = getClientAddress(headers);
        
        logger.info("gRPC call started: method={}, client={}", methodName, clientAddress);
        long startTime = System.currentTimeMillis();
        
        ServerCall<ReqT, RespT> wrappedCall = new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
            @Override
            public void close(Status status, Metadata trailers) {
                long duration = System.currentTimeMillis() - startTime;
                
                if (status.isOk()) {
                    logger.info("gRPC call completed: method={}, duration={}ms, status={}", 
                        methodName, duration, status.getCode());
                } else {
                    logger.error("gRPC call failed: method={}, duration={}ms, status={}, description={}", 
                        methodName, duration, status.getCode(), status.getDescription());
                }
                
                super.close(status, trailers);
            }
        };
        
        return next.startCall(wrappedCall, headers);
    }
    
    private String getClientAddress(Metadata headers) {
        return headers.get(Metadata.Key.of("x-forwarded-for", Metadata.ASCII_STRING_MARSHALLER));
    }
}