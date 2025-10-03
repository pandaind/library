package com.library.bookservice.repository;

import com.library.bookservice.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    List<Book> findByTitleContainingIgnoreCase(String title);

    List<Book> findByAuthorContainingIgnoreCase(String author);

    List<Book> findByGenreIgnoreCase(String genre);

    List<Book> findByAvailableCopiesGreaterThan(Integer copies);

    @Query("SELECT b FROM Book b WHERE b.availableCopies > 0")
    List<Book> findAllAvailableBooks();

    @Query("SELECT b FROM Book b WHERE " +
           "(:searchType = 'title' AND LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%'))) OR " +
           "(:searchType = 'author' AND LOWER(b.author) LIKE LOWER(CONCAT('%', :query, '%'))) OR " +
           "(:searchType = 'isbn' AND b.isbn = :query) OR " +
           "(:searchType = 'genre' AND LOWER(b.genre) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Book> searchBooks(@Param("query") String query, 
                          @Param("searchType") String searchType, 
                          Pageable pageable);

    @Query("SELECT b FROM Book b WHERE " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.author) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.genre) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "b.isbn = :query")
    Page<Book> searchBooksGeneral(@Param("query") String query, Pageable pageable);

    @Query("SELECT COUNT(b) FROM Book b WHERE b.availableCopies > 0")
    long countAvailableBooks();

    @Query("SELECT b FROM Book b WHERE b.publicationYear BETWEEN :startYear AND :endYear")
    List<Book> findBooksByPublicationYearRange(@Param("startYear") Integer startYear, 
                                             @Param("endYear") Integer endYear);

    @Query("SELECT DISTINCT b.genre FROM Book b WHERE b.genre IS NOT NULL ORDER BY b.genre")
    List<String> findAllGenres();

    @Query("SELECT b FROM Book b WHERE b.totalCopies = b.availableCopies")
    List<Book> findNeverBorrowedBooks();
}