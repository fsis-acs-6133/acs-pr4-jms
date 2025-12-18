package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "authors")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Введите ФИО автора")
    @Size(max = 200, message = "Максимум 200 символов")
    @Column(name = "full_name", nullable = false, length = 200)
    private String fullName;

    @Column(name = "birth_year")
    private Integer birthYear;
}
