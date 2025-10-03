package com.library.apigateway.config;

import com.library.bookservice.grpc.BookServiceGrpc;
import com.library.userservice.grpc.UserServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "test.grpc.client.disabled", havingValue = "false", matchIfMissing = true)
public class GrpcClientConfig {

    @Value("${grpc.client.book-service.address:localhost:6565}")
    private String bookServiceAddress;

    @Value("${grpc.client.user-service.address:localhost:6566}")
    private String userServiceAddress;

    @Bean
    public ManagedChannel bookServiceChannel() {
        return ManagedChannelBuilder.forTarget(bookServiceAddress.replace("static://", ""))
                .usePlaintext()
                .build();
    }

    @Bean
    public ManagedChannel userServiceChannel() {
        return ManagedChannelBuilder.forTarget(userServiceAddress.replace("static://", ""))
                .usePlaintext()
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public BookServiceGrpc.BookServiceBlockingStub bookServiceBlockingStub() {
        return BookServiceGrpc.newBlockingStub(bookServiceChannel());
    }

    @Bean
    @ConditionalOnMissingBean
    public UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub() {
        return UserServiceGrpc.newBlockingStub(userServiceChannel());
    }
}