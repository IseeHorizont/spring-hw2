package com.edu.ulab.app.entity;



import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Entity
@Table(name = "person")
@Data
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long userId;

    @Column(name = "full_name")
    @NotBlank(message = "Name must not be blank")
    private String fullName;

    @Column(name = "title")
    @NotBlank(message = "Title must not be blank")
    private String title;

    @Column(name = "age")
    @NotNull(message = "Age should be")
    @Min(value = 0, message = "Age should be 0 or more")
    private int age;
}
