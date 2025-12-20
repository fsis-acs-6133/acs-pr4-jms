package com.example.demo.api.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.*;

@JacksonXmlRootElement(localName = "author")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AuthorDto {
    private Long id;
    private String fullName;
    private Integer birthYear;
}
