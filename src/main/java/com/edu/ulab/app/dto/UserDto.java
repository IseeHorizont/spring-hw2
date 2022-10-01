package com.edu.ulab.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "Entity UserDto")
public class UserDto {

    @Schema(description = "User's identifier")
    private Long userId;

    @NotBlank(message = "Name must not be blank")
    @Schema(description = "User's first name & last name", example = "John Dow")
    private String fullName;

    @NotBlank(message = "Title must not be blank")
    @Schema(description = "User's title", example = "Reader")
    private String title;

    @NotNull(message = "Age should be")
    @Min(value = 0, message = "Age should be 0 or more")
    @Schema(description = "User's age", example = "20")
    private int age;
}
