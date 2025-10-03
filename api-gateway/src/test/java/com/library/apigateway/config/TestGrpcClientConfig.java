package com.library.apigateway.config;

import com.library.bookservice.grpc.BookServiceGrpc;
import com.library.userservice.grpc.UserServiceGrpc;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestGrpcClientConfig {

    @Bean
    @Primary
    public BookServiceGrpc.BookServiceBlockingStub bookServiceBlockingStub() {
        return Mockito.mock(BookServiceGrpc.BookServiceBlockingStub.class);
    }

    @Bean
    @Primary
    public UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub() {
        return Mockito.mock(UserServiceGrpc.UserServiceBlockingStub.class);
    }
}