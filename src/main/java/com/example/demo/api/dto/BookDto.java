package com.example.demo.api.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.*;

@JacksonXmlRootElement(localName = "book")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class BookDto {
    private Long id;
    private String title;
    private Integer publishedYear;
    private AuthorShortDto author; // может быть null если автора удалили
}
