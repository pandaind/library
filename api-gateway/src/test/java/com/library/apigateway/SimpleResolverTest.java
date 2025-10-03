package com.library.apigateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import com.library.apigateway.resolver.BookQueryResolver;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-web-test.properties") 
@Import(com.library.apigateway.config.TestGrpcClientConfig.class)
public class SimpleResolverTest {

    @Autowired
    private ApplicationContext context;

    @Test
    public void testResolverIsRegistered() {
        // Check if the resolver bean exists
        assertThat(context.containsBean("bookQueryResolver")).isTrue();
        
        BookQueryResolver resolver = context.getBean(BookQueryResolver.class);
        assertThat(resolver).isNotNull();
        
        System.out.println("BookQueryResolver found: " + resolver);
    }
}