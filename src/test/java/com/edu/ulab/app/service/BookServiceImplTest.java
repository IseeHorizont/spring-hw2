package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.BookServiceException;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.exception.UserServiceException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.service.impl.BookServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Тестирование функционала {@link com.edu.ulab.app.service.impl.BookServiceImpl}.
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DisplayName("Testing book functionality.")
public class BookServiceImplTest {
    @InjectMocks
    BookServiceImpl bookService;

    @Mock
    BookRepository bookRepository;

    @Mock
    BookMapper bookMapper;

    @Test
    @DisplayName("Create book. Should be successful.")
    void saveBook_success_Test() {
        //given
        Person person  = new Person();
        person.setUserId(1L);

        BookDto bookDto = new BookDto();
        bookDto.setUserId(1L);
        bookDto.setAuthor("test author");
        bookDto.setTitle("test title");
        bookDto.setPageCount(1000);

        BookDto result = new BookDto();
        result.setId(1L);
        result.setUserId(1L);
        result.setAuthor("test author");
        result.setTitle("test title");
        result.setPageCount(1000);

        Book book = new Book();
        book.setPageCount(1000);
        book.setTitle("test title");
        book.setAuthor("test author");
        book.setUserId(person.getUserId());

        Book savedBook = new Book();
        savedBook.setId(1L);
        savedBook.setPageCount(1000);
        savedBook.setTitle("test title");
        savedBook.setAuthor("test author");
        savedBook.setUserId(person.getUserId());

        //when

        when(bookMapper.bookDtoToBook(bookDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(savedBook);
        when(bookMapper.bookToBookDto(savedBook)).thenReturn(result);

        //then
        BookDto bookDtoResult = bookService.createBook(bookDto);
        assertEquals(1L, bookDtoResult.getId());
    }

    @Test
    @DisplayName("Updating book. Should be successful.")
    void updateBook_success_Test () {
        // given

        Book book = new Book();
        book.setId(1L);
        book.setUserId(1L);
        book.setTitle("Title");
        book.setAuthor("Author");
        book.setPageCount(100);

        BookDto bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setUserId(1L);
        bookDto.setTitle("Title");
        bookDto.setAuthor("Author");
        bookDto.setPageCount(100);

        Book updatedBook = new Book();
        book.setId(1L);
        book.setUserId(1L);
        book.setTitle("Title updated");
        book.setAuthor("Author updated");
        book.setPageCount(100);

        BookDto updatedBookDto = new BookDto();
        updatedBookDto.setId(1L);
        updatedBookDto.setUserId(1L);
        updatedBookDto.setTitle("Title updated");
        updatedBookDto.setAuthor("Author updated");
        updatedBookDto.setPageCount(100);

        // when

        when(bookMapper.bookDtoToBook(bookDto)).thenReturn(book);
        when(bookRepository.findByIdForUpdate(book.getId())).thenReturn(Optional.ofNullable(book));
        when(bookMapper.bookToBookDto(updatedBook)).thenReturn(updatedBookDto);

        // then

        BookDto bookDtoResult = bookService.updateBook(updatedBookDto.getId(), updatedBookDto);
        assertNotNull(bookDtoResult);
        assertEquals(bookDto, bookDtoResult);
        verify(bookRepository).save(book);
    }

    @Test
    @DisplayName("Getting book. Should be successful.")
    void getBook_success_Test () {
        // given

        Person user = new Person();
        user.setUserId(1L);

        Book bookWhichLookFor = new Book();
        bookWhichLookFor.setId(1L);
        bookWhichLookFor.setUserId(user.getUserId());
        bookWhichLookFor.setTitle("Title");
        bookWhichLookFor.setAuthor("Author");
        bookWhichLookFor.setPageCount(1000);

        BookDto resultBookDto = new BookDto();
        resultBookDto.setId(1L);
        resultBookDto.setUserId(user.getUserId());
        resultBookDto.setTitle("Title");
        resultBookDto.setAuthor("Author");
        resultBookDto.setPageCount(1000);

        // when

        when(bookRepository.findById(bookWhichLookFor.getId()))
                .thenReturn(Optional.ofNullable(bookWhichLookFor));
        when(bookMapper.bookToBookDto(bookWhichLookFor)).thenReturn(resultBookDto);

        // then

        BookDto resultBookDtoFromService = bookService.getBookById(bookWhichLookFor.getId());
        assertNotNull(resultBookDtoFromService);
        assertEquals(resultBookDto, resultBookDtoFromService);
        verify(bookRepository).findById(bookWhichLookFor.getId());
    }

    @Test
    @DisplayName("Getting book. Expected not found book.")
    void getBook_bookNotFound_Test () {
        // given

        Person user = new Person();
        user.setUserId(1L);

        Book bookWhichLookFor = new Book();
        bookWhichLookFor.setId(11L);
        bookWhichLookFor.setUserId(user.getUserId());

        // when

        BookServiceException bookNotFoundException = new BookServiceException("Book with id #" +
                                                        bookWhichLookFor.getId() + " not found");
        doThrow(bookNotFoundException).when(bookRepository)
                .deleteById(bookWhichLookFor.getId());

        // then

        assertThatThrownBy(() -> bookService.getBookById(bookWhichLookFor.getId()))
                .isInstanceOf(BookServiceException.class)
                .hasMessage("Book with id #" + bookWhichLookFor.getId() + " not found");
        verify(bookRepository).findById(bookWhichLookFor.getId());
    }

    @Test
    @DisplayName("Getting all books. Should be successful.")
    void getAllBooks_Test() {
        // given
        Person user = new Person();
        user.setUserId(1L);

        Book firstBook = new Book();
        firstBook.setId(1L);
        firstBook.setUserId(user.getUserId());
        firstBook.setTitle("First book's title");
        firstBook.setAuthor("First book's author");
        firstBook.setPageCount(150);

        BookDto firstBookDto = new BookDto();
        firstBookDto.setId(1L);
        firstBookDto.setUserId(user.getUserId());
        firstBookDto.setTitle("First book's title");
        firstBookDto.setAuthor("First book's author");
        firstBookDto.setPageCount(150);

        List<Book> allBooks = new ArrayList<>();
        allBooks.add(firstBook);

        List<BookDto> allBookDto = new ArrayList<>();
        allBookDto.add(bookService.createBook(firstBookDto));

        // when

        when(bookRepository.findAllBooksByUserId(user.getUserId())).thenReturn(allBooks);

        // then

        List<BookDto> resultBooksList = bookService.getAllBooksByUserId(user.getUserId());
        assertNotNull(resultBooksList);
        assertEquals(allBookDto, resultBooksList);
        verify(bookRepository).findAllBooksByUserId(user.getUserId());
    }

    @Test
    @DisplayName("Deleting book. Should be successful.")
    void deleteBook_success_Test() {
        // given

        Person user = new Person();
        user.setUserId(1L);

        Book bookForDelete = new Book();
        bookForDelete.setId(1L);
        bookForDelete.setUserId(user.getUserId());
        bookForDelete.setTitle("Title");
        bookForDelete.setAuthor("Author");
        bookForDelete.setPageCount(1000);

        BookDto bookDtoForDelete = bookMapper.bookToBookDto(bookForDelete);
        bookService.createBook(bookDtoForDelete);

        // when

        when(bookRepository.findById(bookForDelete.getId()))
                .thenReturn(Optional.ofNullable(bookForDelete));

        // then

        bookService.deleteBookById(bookForDelete.getId());
        verify(bookRepository).deleteById(bookForDelete.getId());
    }

    @Test
    @DisplayName("Deleting book. Book not found.")
    void deleteBook_bookNotFound_Test() {
        // given

        Book bookForDelete = new Book();
        bookForDelete.setId(22L);

        // when

        NotFoundException bookNotFoundException = new NotFoundException("Book not found");
        doThrow(bookNotFoundException).when(bookRepository)
                .deleteById(bookForDelete.getId());

        // then

        assertThatThrownBy(() -> bookService.deleteBookById(bookForDelete.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Book not found");
        verify(bookRepository).deleteById(bookForDelete.getId());
    }
}
