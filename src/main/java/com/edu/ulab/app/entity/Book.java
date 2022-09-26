package com.edu.ulab.app.entity;


import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "book")
@Data
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "title")
    @NotBlank(message = "Title must be not blank")
    private String title;

    @Column(name = "author")
    @NotBlank(message = "Author must be not blank")
    private String author;

    @Column(name = "page_count")
    @NotNull
    @Min(value = 1, message = "Page count should be 1 or more")
    private long pageCount;


}
