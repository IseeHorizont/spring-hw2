package com.edu.ulab.app.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UserDto {

    private Long userId;

    @NotBlank(message = "Name must not be blank")
    private String fullName;

    @NotBlank(message = "Title must not be blank")
    private String title;

    @NotNull(message = "Age should be")
    @Min(value = 0, message = "Age should be 0 or more")
    private int age;
}
