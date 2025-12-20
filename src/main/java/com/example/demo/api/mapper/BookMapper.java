package com.example.demo.api.mapper;

import com.example.demo.api.dto.AuthorShortDto;
import com.example.demo.api.dto.BookDto;
import com.example.demo.model.Author;
import com.example.demo.model.Book;

public class BookMapper {

    private BookMapper() {}

    public static BookDto toDto(Book b) {
        Author a = b.getAuthor();

        AuthorShortDto authorDto = null;
        if (a != null) {
            authorDto = AuthorShortDto.builder()
                    .id(a.getId())
                    .fullName(a.getFullName())
                    .build();
        }

        return BookDto.builder()
                .id(b.getId())
                .title(b.getTitle())
                .publishedYear(b.getPublishedYear())
                .author(authorDto)
                .build();
    }
}
