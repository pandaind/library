package com.library.bookservice.config;

import com.library.bookservice.entity.Book;
import com.library.bookservice.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final BookRepository bookRepository;

    @Override
    public void run(String... args) {
        if (bookRepository.count() == 0) {
            log.info("Initializing sample book data...");
            initializeSampleBooks();
            log.info("Sample book data initialization completed.");
        } else {
            log.info("Book data already exists, skipping initialization.");
        }
    }

    private void initializeSampleBooks() {
        // Fiction Books
        Book book1 = Book.builder()
                .title("The Great Gatsby")
                .author("F. Scott Fitzgerald")
                .isbn("978-0-7432-7356-5")
                .publisher("Scribner")
                .publicationYear(1925)
                .genre("Fiction")
                .totalCopies(5)
                .availableCopies(5)
                .description("A classic American novel set in the Jazz Age")
                .language("English")
                .pages(180)
                .build();

        Book book2 = Book.builder()
                .title("To Kill a Mockingbird")
                .author("Harper Lee")
                .isbn("978-0-06-112008-4")
                .publisher("J.B. Lippincott & Co.")
                .publicationYear(1960)
                .genre("Fiction")
                .totalCopies(3)
                .availableCopies(3)
                .description("A gripping tale of racial injustice and childhood in the American South")
                .language("English")
                .pages(376)
                .build();

        Book book3 = Book.builder()
                .title("1984")
                .author("George Orwell")
                .isbn("978-0-452-28423-4")
                .publisher("Secker & Warburg")
                .publicationYear(1949)
                .genre("Dystopian Fiction")
                .totalCopies(4)
                .availableCopies(4)
                .description("A dystopian social science fiction novel")
                .language("English")
                .pages(328)
                .build();

        // Science Fiction
        Book book4 = Book.builder()
                .title("Dune")
                .author("Frank Herbert")
                .isbn("978-0-441-17271-9")
                .publisher("Chilton Books")
                .publicationYear(1965)
                .genre("Science Fiction")
                .totalCopies(2)
                .availableCopies(2)
                .description("Epic science fiction novel set on the desert planet Arrakis")
                .language("English")
                .pages(688)
                .build();

        Book book5 = Book.builder()
                .title("The Hitchhiker's Guide to the Galaxy")
                .author("Douglas Adams")
                .isbn("978-0-345-39180-3")
                .publisher("Pan Books")
                .publicationYear(1979)
                .genre("Science Fiction")
                .totalCopies(3)
                .availableCopies(3)
                .description("A comedic science fiction series")
                .language("English")
                .pages(224)
                .build();

        // Mystery
        Book book6 = Book.builder()
                .title("The Murder of Roger Ackroyd")
                .author("Agatha Christie")
                .isbn("978-0-00-712448-5")
                .publisher("William Collins, Sons")
                .publicationYear(1926)
                .genre("Mystery")
                .totalCopies(3)
                .availableCopies(3)
                .description("A classic Hercule Poirot mystery")
                .language("English")
                .pages(288)
                .build();

        // Non-Fiction
        Book book7 = Book.builder()
                .title("Sapiens: A Brief History of Humankind")
                .author("Yuval Noah Harari")
                .isbn("978-0-06-231609-7")
                .publisher("Harvill Secker")
                .publicationYear(2011)
                .genre("Non-Fiction")
                .totalCopies(4)
                .availableCopies(4)
                .description("An exploration of the history and impact of Homo sapiens")
                .language("English")
                .pages(443)
                .build();

        Book book8 = Book.builder()
                .title("Educated")
                .author("Tara Westover")
                .isbn("978-0-399-59050-4")
                .publisher("Random House")
                .publicationYear(2018)
                .genre("Memoir")
                .totalCopies(2)
                .availableCopies(2)
                .description("A memoir about education and family")
                .language("English")
                .pages(334)
                .build();

        // Fantasy
        Book book9 = Book.builder()
                .title("The Lord of the Rings")
                .author("J.R.R. Tolkien")
                .isbn("978-0-544-00341-5")
                .publisher("George Allen & Unwin")
                .publicationYear(1954)
                .genre("Fantasy")
                .totalCopies(6)
                .availableCopies(6)
                .description("Epic high fantasy novel")
                .language("English")
                .pages(1216)
                .build();

        Book book10 = Book.builder()
                .title("Harry Potter and the Philosopher's Stone")
                .author("J.K. Rowling")
                .isbn("978-0-7475-3269-9")
                .publisher("Bloomsbury")
                .publicationYear(1997)
                .genre("Fantasy")
                .totalCopies(5)
                .availableCopies(4) // One copy borrowed
                .description("First book in the Harry Potter series")
                .language("English")
                .pages(223)
                .build();

        // Save all books
        bookRepository.save(book1);
        bookRepository.save(book2);
        bookRepository.save(book3);
        bookRepository.save(book4);
        bookRepository.save(book5);
        bookRepository.save(book6);
        bookRepository.save(book7);
        bookRepository.save(book8);
        bookRepository.save(book9);
        bookRepository.save(book10);

        log.info("Created {} sample books", 10);
    }
}