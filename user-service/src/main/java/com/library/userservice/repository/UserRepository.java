package com.library.userservice.repository;

import com.library.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    List<User> findByStatus(User.UserStatus status);

    List<User> findByMembershipType(User.MembershipType membershipType);

    @Query("SELECT u FROM User u WHERE u.status = 'ACTIVE'")
    List<User> findAllActiveUsers();

    @Query("SELECT u FROM User u WHERE u.outstandingFines > 0")
    List<User> findUsersWithFines();

    @Query("SELECT u FROM User u WHERE u.currentBorrowedBooks > 0")
    List<User> findUsersWithBorrowedBooks();

    @Query("SELECT u FROM User u WHERE u.currentBorrowedBooks >= u.maxBooksAllowed")
    List<User> findUsersAtBorrowLimit();

    @Query("SELECT u FROM User u WHERE u.email LIKE LOWER(CONCAT('%', :email, '%'))")
    List<User> findByEmailContaining(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<User> findByNameContaining(@Param("name") String name);

    @Query("SELECT COUNT(u) FROM User u WHERE u.status = 'ACTIVE'")
    long countActiveUsers();

    @Query("SELECT COUNT(u) FROM User u WHERE u.membershipType = :membershipType")
    long countUsersByMembershipType(@Param("membershipType") User.MembershipType membershipType);

    @Query("SELECT u FROM User u JOIN u.borrowedBookIds b WHERE b = :bookId")
    List<User> findUsersByBorrowedBookId(@Param("bookId") String bookId);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}