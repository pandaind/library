package com.library.apigateway.config;

import com.library.apigateway.dto.Book;
import com.library.apigateway.dto.User;
import graphql.execution.instrumentation.dataloader.DataLoaderDispatcherInstrumentation;
import graphql.execution.instrumentation.dataloader.DataLoaderDispatcherInstrumentationOptions;
import lombok.RequiredArgsConstructor;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataLoaderRegistryConfig {

    private final DataLoader<String, User> userDataLoader;
    private final DataLoader<String, Book> bookDataLoader;

    @Bean
    public DataLoaderRegistry dataLoaderRegistry() {
        DataLoaderRegistry registry = new DataLoaderRegistry();
        registry.register("userDataLoader", userDataLoader);
        registry.register("bookDataLoader", bookDataLoader);
        return registry;
    }

    @Bean
    public DataLoaderDispatcherInstrumentation dataLoaderDispatcherInstrumentation() {
        return new DataLoaderDispatcherInstrumentation(
            DataLoaderDispatcherInstrumentationOptions.newOptions()
                .includeStatistics(true)
        );
    }
}