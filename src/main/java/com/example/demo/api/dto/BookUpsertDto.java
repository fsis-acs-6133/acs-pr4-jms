package com.example.demo.api.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@JacksonXmlRootElement(localName = "book")
@Getter
@Setter
public class BookUpsertDto {

    @NotBlank(message = "Введите название книги")
    @Size(max = 300, message = "Максимум 300 символов")
    private String title;

    private Integer publishedYear;

    @NotNull(message = "Выберите автора")
    private Long authorId;
}
