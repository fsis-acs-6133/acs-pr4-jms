package com.example.demo.api.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AuthorShortDto {
    private Long id;
    private String fullName;
}
