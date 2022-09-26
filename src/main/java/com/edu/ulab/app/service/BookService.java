package com.edu.ulab.app.service;


import com.edu.ulab.app.dto.BookDto;

import java.util.List;

public interface BookService {
    BookDto createBook(BookDto bookDto);

    BookDto updateBook(BookDto bookDto);

    List<BookDto> getAllBooksByUserId(Long userId);

    BookDto getBookById(Long id);

    void deleteBookById(Long id);
}
