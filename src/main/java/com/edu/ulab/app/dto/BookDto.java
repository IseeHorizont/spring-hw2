package com.edu.ulab.app.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class BookDto {

    private Long id;

    private Long userId;

    @NotBlank(message = "Title must be not blank")
    private String title;

    @NotBlank(message = "Author must be not blank")
    private String author;

    @NotNull
    @Min(value = 1, message = "Page count should be 1 or more")
    private long pageCount;
}
