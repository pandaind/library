package com.library.apigateway.mapper;

import com.library.apigateway.dto.BorrowRecord;
import com.library.userservice.grpc.BorrowHistory;
import com.library.apigateway.enums.UserStatus;
import com.library.apigateway.enums.MembershipType;
import com.library.apigateway.enums.BorrowStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserMapper {

    public com.library.apigateway.dto.User toUser(com.library.userservice.grpc.User protoUser) {
        if (protoUser == null) {
            return null;
        }
        
        return com.library.apigateway.dto.User.builder()
                .id(protoUser.getId())
                .username(protoUser.getUsername())
                .email(protoUser.getEmail())
                .firstName(protoUser.getFirstName())
                .lastName(protoUser.getLastName())
                .phone(protoUser.getPhone().isEmpty() ? null : protoUser.getPhone())
                .address(protoUser.getAddress().isEmpty() ? null : protoUser.getAddress())
                .registrationDate(LocalDateTime.ofEpochSecond(protoUser.getRegistrationDate(), 0, ZoneOffset.UTC))
                .status(UserStatus.valueOf(protoUser.getStatus()))
                .membershipType(MembershipType.valueOf(protoUser.getMembershipType()))
                .maxBooksAllowed(protoUser.getMaxBooksAllowed())
                .currentBorrowedBooks(protoUser.getCurrentBorrowedBooks())
                .outstandingFines((float) protoUser.getOutstandingFines())
                .borrowedBookIds(new ArrayList<>()) // Will be populated from borrow history
                .borrowedBooks(new ArrayList<>()) // Will be populated via field resolver
                .build();
    }
    
    public BorrowRecord toBorrowRecord(BorrowHistory protoBorrowHistory) {
        if (protoBorrowHistory == null) {
            return null;
        }
        
        return BorrowRecord.builder()
                .transactionId(protoBorrowHistory.getTransactionId())
                .bookId(protoBorrowHistory.getBookId())
                .bookTitle(protoBorrowHistory.getBookTitle())
                .borrowDate(LocalDateTime.ofEpochSecond(protoBorrowHistory.getBorrowDate(), 0, ZoneOffset.UTC))
                .dueDate(LocalDateTime.ofEpochSecond(protoBorrowHistory.getDueDate(), 0, ZoneOffset.UTC))
                .returnDate(protoBorrowHistory.getReturnDate() == 0 ? null : 
                           LocalDateTime.ofEpochSecond(protoBorrowHistory.getReturnDate(), 0, ZoneOffset.UTC))
                .status(BorrowStatus.valueOf(protoBorrowHistory.getStatus()))
                .fineAmount((float) protoBorrowHistory.getFineAmount())
                .build();
    }
    
    public List<BorrowRecord> toBorrowRecords(List<BorrowHistory> protoBorrowHistories) {
        if (protoBorrowHistories == null) {
            return new ArrayList<>();
        }
        
        return protoBorrowHistories.stream()
                .map(this::toBorrowRecord)
                .toList();
    }
}