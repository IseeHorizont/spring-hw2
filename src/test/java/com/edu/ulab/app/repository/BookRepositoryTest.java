package com.edu.ulab.app.repository;

import com.edu.ulab.app.config.SystemJpaTest;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.List;

import static com.vladmihalcea.sql.SQLStatementCountValidator.*;
import static com.vladmihalcea.sql.SQLStatementCountValidator.assertDeleteCount;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Тесты репозитория {@link BookRepository}.
 */
@SystemJpaTest
public class BookRepositoryTest {
    @Autowired
    BookRepository bookRepository;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        SQLStatementCountValidator.reset();
    }

    @DisplayName("Saving book")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void insertUserAndBook_thenAssertDmlCount() {
        //Given

        Person person = new Person();
        person.setAge(111);
        person.setTitle("reader");
        person.setFullName("Test Test");

        Person savedPerson = userRepository.save(person);

        Book book = new Book();
        book.setAuthor("Test Author");
        book.setTitle("test");
        book.setPageCount(1000);
        book.setUserId(savedPerson.getUserId());

        //When
        Book result = bookRepository.save(book);

        //Then
        assertNotNull(result);
        assertThat(result.getPageCount()).isEqualTo(1000);
        assertThat(result.getTitle()).isEqualTo("test");
        assertSelectCount(0);
        assertInsertCount(2);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Updating book")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void updateBook() {
        // Given
        Person createdUser = new Person();
        createdUser.setUserId(1L);

        Book createdBook = new Book();
        createdBook.setUserId(createdUser.getUserId());
        createdBook.setTitle("Title");
        createdBook.setAuthor("Author");
        createdBook.setPageCount(1000);

        bookRepository.save(createdBook);

        Book updatedBook = new Book();
        updatedBook.setUserId(createdUser.getUserId());
        updatedBook.setTitle("Updated title");
        updatedBook.setAuthor("Updated author");
        updatedBook.setPageCount(1100);

        // When
        Book result = bookRepository.save(updatedBook);

        // Then
        assertNotNull(result);
        assertThat(result.getTitle()).isEqualTo(updatedBook.getTitle());
        assertThat(result.getAuthor()).isEqualTo(updatedBook.getAuthor());
        assertThat(result.getPageCount()).isEqualTo(updatedBook.getPageCount());

        assertSelectCount(0);
        assertInsertCount(2);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Getting book by user's id")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void getBookByUserId() {
        // Given
        Person user = new Person();
        user.setUserId(1L);

        Book book = new Book();
        book.setUserId(user.getUserId());
        book.setTitle("Title");
        book.setAuthor("Author");
        book.setPageCount(1000);

        bookRepository.save(book);

        // When
        Book foundBook = bookRepository.findById(book.getId()).get();

        // Then
        assertThat(foundBook).isNotNull();
        assertThat(foundBook.getUserId()).isEqualTo(book.getUserId());
        assertThat(foundBook.getTitle()).isEqualTo(book.getTitle());
        assertThat(foundBook.getAuthor()).isEqualTo(book.getAuthor());
        assertThat(foundBook.getPageCount()).isEqualTo(book.getPageCount());

        assertInsertCount(1);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Getting all books by user's id")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void getAllBookByUserId() {
        // Given
        Person user = new Person();
        user.setUserId(1L);

        Book book = new Book();
        book.setUserId(user.getUserId());
        book.setTitle("Title");
        book.setAuthor("Author");
        book.setPageCount(1000);

        bookRepository.save(book);

        List<Book> listWithRightBooks = new ArrayList<>();
        listWithRightBooks.add(book);

        // When
        List<Book> resultBooksList = bookRepository.findAllBooksByUserId(user.getUserId());

        // Then
        assertNotNull(resultBooksList);
        assertThat(resultBooksList.size()).isEqualTo(1);
        assertThat(resultBooksList).isEqualTo(listWithRightBooks);

        assertSelectCount(1);
        assertInsertCount(1);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Deleting book by id")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void deleteBookById() {
        // Given
        Person user = new Person();
        user.setUserId(1L);

        Book book = new Book();
        book.setUserId(user.getUserId());
        book.setTitle("Title");
        book.setAuthor("Author");
        book.setPageCount(1000);

        bookRepository.save(book);

        // When
        bookRepository.deleteById(book.getId());

        // Then
        // count = 2, потому что в репо остались ещё две книги
        assertThat(bookRepository.count()).isEqualTo(2);

        assertSelectCount(1);
        assertInsertCount(1);
        assertUpdateCount(0);
        assertDeleteCount(1);
    }

}
