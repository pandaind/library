package com.library.apigateway;

import com.library.apigateway.dto.User;
import com.library.apigateway.enums.UserStatus;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ResolverIntegrationTest {

    @Test
    public void testApplicationContextLoads() {
        // Simple test to verify application context loads successfully
    }

    @Test
    public void testUserBuilder() {
        // Test basic User builder functionality
        User user = User.builder()
                .id("1")
                .firstName("John")
                .lastName("Doe")
                .status(UserStatus.ACTIVE)
                .maxBooksAllowed(5)
                .currentBorrowedBooks(2)
                .outstandingFines(15.50f)
                .build();

        assertThat(user.getFullName()).isEqualTo("John Doe");
        assertThat(user.getCanBorrowMore()).isTrue();
        assertThat(user.getHasFines()).isTrue();
    }
}