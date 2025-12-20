package com.example.demo.api.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@JacksonXmlRootElement(localName = "author")
@Getter
@Setter
public class AuthorUpsertDto {

    @NotBlank(message = "Введите ФИО автора")
    @Size(max = 200, message = "Максимум 200 символов")
    private String fullName;

    private Integer birthYear;
}
