package com.library.apigateway.config;

import com.library.apigateway.exception.CustomDataFetcherExceptionHandler;
import graphql.execution.DataFetcherExceptionHandler;
import graphql.schema.GraphQLScalarType;
import graphql.scalars.ExtendedScalars;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class GraphQLConfig {

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder
                .scalar(dateTimeScalar())
                .scalar(longScalar());
    }

    @Bean
    public GraphQLScalarType dateTimeScalar() {
        return GraphQLScalarType.newScalar()
                .name("DateTime")
                .description("Java LocalDateTime as scalar")
                .coercing(new graphql.schema.Coercing<LocalDateTime, String>() {
                    @Override
                    public String serialize(Object dataFetcherResult) {
                        if (dataFetcherResult instanceof LocalDateTime) {
                            return ((LocalDateTime) dataFetcherResult).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                        }
                        return null;
                    }

                    @Override
                    public LocalDateTime parseValue(Object input) {
                        if (input instanceof String) {
                            return LocalDateTime.parse((String) input, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                        }
                        return null;
                    }

                    @Override
                    public LocalDateTime parseLiteral(Object input) {
                        if (input instanceof graphql.language.StringValue) {
                            return LocalDateTime.parse(((graphql.language.StringValue) input).getValue(), 
                                    DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                        }
                        return null;
                    }
                })
                .build();
    }

    @Bean
    public GraphQLScalarType longScalar() {
        return ExtendedScalars.GraphQLLong;
    }
    
    @Bean
    public DataFetcherExceptionHandler dataFetcherExceptionHandler() {
        return new CustomDataFetcherExceptionHandler();
    }
    
    @Bean
    public DataFetcherExceptionResolverAdapter exceptionResolver() {
        return new DataFetcherExceptionResolverAdapter() {
            @Override
            protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
                return GraphqlErrorBuilder.newError()
                        .errorType(graphql.ErrorType.DataFetchingException)
                        .message(ex.getMessage())
                        .path(env.getExecutionStepInfo().getPath())
                        .location(env.getField().getSourceLocation())
                        .build();
            }
        };
    }
}