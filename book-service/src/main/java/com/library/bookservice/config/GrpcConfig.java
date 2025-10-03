package com.library.bookservice.config;

import com.library.bookservice.interceptor.ErrorHandlingInterceptor;
import com.library.bookservice.interceptor.LoggingInterceptor;
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