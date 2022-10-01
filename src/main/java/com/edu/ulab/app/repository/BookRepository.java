package com.edu.ulab.app.repository;

import com.edu.ulab.app.entity.Book;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PathVariable;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface BookRepository extends CrudRepository<Book, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from Book b where b.id = :id")
    Optional<Book> findByIdForUpdate(@Param("id") Long id);

    @Query("select b from Book b where b.userId = :user_id")
    List<Book> findAllBooksByUserId(@Param("user_id") Long userId);

    @Query("select b from Book b where b.userId = :#{#book.userId} " +
            "and b.title = :#{#book.title} " +
            "and b.author = :#{#book.author} " +
            "and b.pageCount = :#{#book.pageCount}")
    Optional<Book> findBookByParamWithoutId(@Param("book") Book book);
}
