package com.example.demo.api.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.*;

import java.util.List;

@JacksonXmlRootElement(localName = "authors")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class AuthorsXml {

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "author")
    private List<AuthorDto> authors;
}
