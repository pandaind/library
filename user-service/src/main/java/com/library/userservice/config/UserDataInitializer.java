package com.library.userservice.config;

import com.library.userservice.entity.User;
import com.library.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            log.info("Initializing sample user data...");
            initializeSampleUsers();
            log.info("Sample user data initialization completed.");
        } else {
            log.info("User data already exists, skipping initialization.");
        }
    }

    private void initializeSampleUsers() {
        // Basic users
        User user1 = User.builder()
                .username("john_doe")
                .email("john.doe@email.com")
                .firstName("John")
                .lastName("Doe")
                .phone("+1-555-0101")
                .address("123 Main St, Springfield, IL 62701")
                .registrationDate(LocalDateTime.now().minusMonths(6))
                .status(User.UserStatus.ACTIVE)
                .membershipType(User.MembershipType.BASIC)
                .maxBooksAllowed(3)
                .currentBorrowedBooks(1)
                .outstandingFines(0.0)
                .borrowedBookIds(List.of("1")) // Has one book borrowed
                .build();

        User user2 = User.builder()
                .username("jane_smith")
                .email("jane.smith@email.com")
                .firstName("Jane")
                .lastName("Smith")
                .phone("+1-555-0102")
                .address("456 Oak Ave, Springfield, IL 62702")
                .registrationDate(LocalDateTime.now().minusMonths(4))
                .status(User.UserStatus.ACTIVE)
                .membershipType(User.MembershipType.PREMIUM)
                .maxBooksAllowed(10)
                .currentBorrowedBooks(3)
                .outstandingFines(5.50)
                .borrowedBookIds(Arrays.asList("2", "3", "4"))
                .build();

        // Student users
        User user3 = User.builder()
                .username("alice_student")
                .email("alice.student@university.edu")
                .firstName("Alice")
                .lastName("Johnson")
                .phone("+1-555-0103")
                .address("789 College Blvd, University City, IL 62703")
                .registrationDate(LocalDateTime.now().minusMonths(2))
                .status(User.UserStatus.ACTIVE)
                .membershipType(User.MembershipType.STUDENT)
                .maxBooksAllowed(5)
                .currentBorrowedBooks(2)
                .outstandingFines(0.0)
                .borrowedBookIds(Arrays.asList("5", "6"))
                .build();

        User user4 = User.builder()
                .username("bob_wilson")
                .email("bob.wilson@email.com")
                .firstName("Bob")
                .lastName("Wilson")
                .phone("+1-555-0104")
                .address("321 Pine St, Springfield, IL 62704")
                .registrationDate(LocalDateTime.now().minusMonths(8))
                .status(User.UserStatus.ACTIVE)
                .membershipType(User.MembershipType.BASIC)
                .maxBooksAllowed(3)
                .currentBorrowedBooks(0)
                .outstandingFines(0.0)
                .borrowedBookIds(List.of()) // No books currently borrowed
                .build();

        User user5 = User.builder()
                .username("carol_brown")
                .email("carol.brown@email.com")
                .firstName("Carol")
                .lastName("Brown")
                .phone("+1-555-0105")
                .address("654 Elm St, Springfield, IL 62705")
                .registrationDate(LocalDateTime.now().minusMonths(3))
                .status(User.UserStatus.SUSPENDED)
                .membershipType(User.MembershipType.BASIC)
                .maxBooksAllowed(3)
                .currentBorrowedBooks(1)
                .outstandingFines(25.00) // High fine amount
                .borrowedBookIds(List.of("7"))
                .build();

        // Premium user with many books
        User user6 = User.builder()
                .username("david_premium")
                .email("david.premium@email.com")
                .firstName("David")
                .lastName("Miller")
                .phone("+1-555-0106")
                .address("987 Maple Dr, Springfield, IL 62706")
                .registrationDate(LocalDateTime.now().minusMonths(12))
                .status(User.UserStatus.ACTIVE)
                .membershipType(User.MembershipType.PREMIUM)
                .maxBooksAllowed(10)
                .currentBorrowedBooks(5)
                .outstandingFines(2.50)
                .borrowedBookIds(Arrays.asList("8", "9", "10", "11", "12"))
                .build();

        // Student with no current borrows
        User user7 = User.builder()
                .username("emma_grad")
                .email("emma.grad@university.edu")
                .firstName("Emma")
                .lastName("Davis")
                .phone("+1-555-0107")
                .address("147 University Ave, University City, IL 62707")
                .registrationDate(LocalDateTime.now().minusMonths(1))
                .status(User.UserStatus.ACTIVE)
                .membershipType(User.MembershipType.STUDENT)
                .maxBooksAllowed(5)
                .currentBorrowedBooks(0)
                .outstandingFines(0.0)
                .borrowedBookIds(List.of())
                .build();

        // Inactive user
        User user8 = User.builder()
                .username("frank_inactive")
                .email("frank.inactive@email.com")
                .firstName("Frank")
                .lastName("Garcia")
                .phone("+1-555-0108")
                .address("258 Birch Ln, Springfield, IL 62708")
                .registrationDate(LocalDateTime.now().minusMonths(18))
                .status(User.UserStatus.INACTIVE)
                .membershipType(User.MembershipType.BASIC)
                .maxBooksAllowed(3)
                .currentBorrowedBooks(0)
                .outstandingFines(0.0)
                .borrowedBookIds(List.of())
                .build();

        // Save all users
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);
        userRepository.save(user5);
        userRepository.save(user6);
        userRepository.save(user7);
        userRepository.save(user8);

        log.info("Created {} sample users", 8);
        log.info("Active users: {}", userRepository.countActiveUsers());
        log.info("Premium members: {}", userRepository.countUsersByMembershipType(User.MembershipType.PREMIUM));
        log.info("Student members: {}", userRepository.countUsersByMembershipType(User.MembershipType.STUDENT));
    }
}