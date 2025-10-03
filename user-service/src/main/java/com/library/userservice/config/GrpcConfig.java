package com.library.userservice.config;

import com.library.userservice.interceptor.ErrorHandlingInterceptor;
import com.library.userservice.interceptor.LoggingInterceptor;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcConfig {
    
    @GrpcGlobalServerInterceptor
    LoggingInterceptor loggingInterceptor() {
        return new LoggingInterceptor();
    }
    
    @GrpcGlobalServerInterceptor
    ErrorHandlingInterceptor errorHandlingInterceptor() {
        return new ErrorHandlingInterceptor();
    }
}