package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.BookServiceException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.mapper.BookRowMapper;
import com.edu.ulab.app.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class BookServiceImplTemplate implements BookService {

    private final JdbcTemplate jdbcTemplate;

    private final BookMapper bookMapper;

    private final String INSERT_SQL = "INSERT INTO BOOK(TITLE, AUTHOR, PAGE_COUNT, USER_ID) VALUES (?,?,?,?)";
    private final String UPDATE_SQL = "UPDATE BOOK SET USER_ID = ?, TITLE = ?, AUTHOR = ?, PAGE_COUNT = ? WHERE ID = ?";
    private final String SELECT_ALL_BOOKS_BY_USER_ID_SQL = "SELECT * FROM BOOK WHERE USER_ID = ?";
    private final String SELECT_BOOK_BY_ID_SQL = "SELECT * FROM BOOK WHERE ID = ?";
    private final String DELETE_SQL = "DELETE FROM BOOK WHERE ID = ?";

    public BookServiceImplTemplate(JdbcTemplate jdbcTemplate, BookMapper bookMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.bookMapper = bookMapper;
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        PreparedStatement ps =
                                connection.prepareStatement(INSERT_SQL, new String[]{"id"});
                        ps.setString(1, bookDto.getTitle());
                        ps.setString(2, bookDto.getAuthor());
                        ps.setLong(3, bookDto.getPageCount());
                        ps.setLong(4, bookDto.getUserId());
                        return ps;
                    }
                },
                keyHolder);

        bookDto.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        log.info("Create book: {}", bookDto);
        return bookDto;
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        Book currentBook = bookMapper.bookDtoToBook(bookDto);
        log.info("Mapped book: {}", currentBook);
        BookDto bookFromDB = getBookById(currentBook.getId());

        jdbcTemplate.update(UPDATE_SQL, currentBook.getUserId(),
                currentBook.getTitle(),
                currentBook.getAuthor(),
                currentBook.getPageCount(),
                bookFromDB.getId());
        bookDto.setId(bookFromDB.getId());
        log.info("Updated book: {}", bookDto);

        return bookDto;
    }

    @Override
    public List<BookDto> getAllBooksByUserId(Long userId) {
        List<Book> allBooksByUserId = jdbcTemplate.query(SELECT_ALL_BOOKS_BY_USER_ID_SQL, new BookRowMapper());
        log.info("All books by userId#{}: {}", userId, allBooksByUserId);

        return allBooksByUserId.stream()
                .map(book -> bookMapper.bookToBookDto(book))
                .toList();
    }

    @Override
    public BookDto getBookById(Long id) {
        BookDto foundBook = jdbcTemplate.query(SELECT_BOOK_BY_ID_SQL, new BookRowMapper(), id)
                .stream()
                .findFirst()
                .map(book -> bookMapper.bookToBookDto(book))
                .orElseThrow(() -> new BookServiceException("Book with id #" + id + " not found"));
        log.info("Found book by id #{}: {}", id, foundBook);

        return foundBook;
    }

    @Override
    public void deleteBookById(Long id) {
        int deletedBooksCount = jdbcTemplate.update(DELETE_SQL, id);
        log.info("How many books deleted: {}", deletedBooksCount);
    }
}
