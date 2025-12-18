package com.example.demo.web.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookForm {

    @NotBlank(message = "Введите название книги")
    @Size(max = 300, message = "Максимум 300 символов")
    private String title;

    private Integer publishedYear;

    // может быть null (например, если автор удалён)
    private Long authorId;
}
