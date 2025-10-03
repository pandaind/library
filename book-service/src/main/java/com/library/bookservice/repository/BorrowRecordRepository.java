package com.library.bookservice.repository;

import com.library.bookservice.entity.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    Optional<BorrowRecord> findByTransactionId(String transactionId);

    List<BorrowRecord> findByUserId(String userId);

    List<BorrowRecord> findByBookId(Long bookId);

    List<BorrowRecord> findByUserIdAndStatus(String userId, BorrowRecord.BorrowStatus status);

    List<BorrowRecord> findByBookIdAndStatus(Long bookId, BorrowRecord.BorrowStatus status);

    @Query("SELECT br FROM BorrowRecord br WHERE br.userId = :userId AND br.book.id = :bookId AND br.status = 'BORROWED'")
    Optional<BorrowRecord> findActiveBorrowRecord(@Param("userId") String userId, @Param("bookId") Long bookId);

    @Query("SELECT br FROM BorrowRecord br WHERE br.status = 'BORROWED' AND br.dueDate < CURRENT_TIMESTAMP")
    List<BorrowRecord> findOverdueRecords();

    @Query("SELECT COUNT(br) FROM BorrowRecord br WHERE br.userId = :userId AND br.status = 'BORROWED'")
    long countActiveBorrowsByUser(@Param("userId") String userId);
}