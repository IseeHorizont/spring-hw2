package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.exception.BookServiceException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    public BookServiceImpl(BookRepository bookRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        Book book = bookMapper.bookDtoToBook(bookDto);
        log.info("Mapped book: {}", book);
        Book savedBook = bookRepository.save(book);
        log.info("Saved book: {}", savedBook);
        return bookMapper.bookToBookDto(savedBook);
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        Book currentBook = bookMapper.bookDtoToBook(bookDto);
        log.info("Mapped book: {}", currentBook);

        Book bookByCurrentParamFromDB = bookRepository.findBookByParamWithoutId(currentBook)
                        .orElse(bookMapper.bookDtoToBook(createBook(bookDto)));
        log.info("Got book from DB for update: {}", bookByCurrentParamFromDB);

        currentBook.setId(bookByCurrentParamFromDB.getId());
        Book updatedBook = bookRepository.save(currentBook);
        log.info("Updated book: {}", updatedBook);

        return bookMapper.bookToBookDto(updatedBook);
    }

    public List<BookDto> getAllBooksByUserId(Long userId) {
        log.info("Got user's id #{} to look for books", userId);
        List<Book> allBooksByUserId = bookRepository.findAllBooksByUserId(userId);
        log.info("All books by user's id: {}", allBooksByUserId);

        return allBooksByUserId.stream()
                .map(book -> bookMapper.bookToBookDto(book))
                .toList();
    }

    @Override
    public BookDto getBookById(Long id) {
        log.info("Got book id #{}", id);
        Book foundBookById = bookRepository.findById(id)
                .orElseThrow(() -> new BookServiceException("User with id #" + id + " not found")
        );
        log.info("Found book by id #{}: {}", id, foundBookById);
        return bookMapper.bookToBookDto(foundBookById);
    }

    @Override
    public void deleteBookById(Long id) {
        log.info("Got book id #{} for delete", id);
        bookRepository.deleteById(id);
        log.info("Deleted book with id #{}", id);
    }
}
