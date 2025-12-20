package com.example.demo.api.mapper;

import com.example.demo.api.dto.AuthorDto;
import com.example.demo.model.Author;

public class AuthorMapper {

    private AuthorMapper() {}

    public static AuthorDto toDto(Author a) {
        return AuthorDto.builder()
                .id(a.getId())
                .fullName(a.getFullName())
                .birthYear(a.getBirthYear())
                .build();
    }
}
