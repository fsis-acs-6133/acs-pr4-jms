package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "books")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Введите название книги")
    @Size(max = 300, message = "Максимум 300 символов")
    @Column(name = "title", nullable = false, length = 300)
    private String title;

    @Column(name = "published_year")
    private Integer publishedYear;

    // автор МОЖЕТ быть null (после удаления автора книги остаются)
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "author_id", nullable = true)
    private Author author;
}
