package com.example.demo.api.web;

import com.example.demo.api.dto.*;
import com.example.demo.api.mapper.AuthorMapper;
import com.example.demo.api.mapper.BookMapper;
import com.example.demo.api.support.XmlWithXsltService;
import com.example.demo.model.Author;
import com.example.demo.service.AuthorService;
import com.example.demo.service.BookService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/authors")
@Tag(name = "Authors REST", description = "CRUD для авторов + получение книг автора. JSON и XML поддерживаются.")
public class AuthorRestController {

    private final AuthorService authorService;
    private final BookService bookService;
    private final XmlWithXsltService xml;

    private boolean wantsXml(String format, HttpServletRequest request) {
        if (format != null && format.equalsIgnoreCase("xml")) return true;

        String accept = request.getHeader("Accept");
        return accept != null && accept.toLowerCase().contains("xml");
    }

    @Operation(
            summary = "Список авторов",
            description = """
                    Возвращает список авторов.
                    - JSON: объект-обёртка { "authors": [...] } (AuthorsXml)
                    - XML: строка XML + добавляется xml-stylesheet PI для отображения в браузере (?format=xml)
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthorsXml.class)),
                    @Content(mediaType = "application/xml",
                            schema = @Schema(type = "string", description = "XML строка с XSL processing-instruction"))
            })
    })
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> list(
            @Parameter(in = ParameterIn.QUERY, name = "format",
                    description = "Если указать format=xml — отдаём XML с XSL (для браузера)",
                    example = "xml")
            @RequestParam(value = "format", required = false) String format,
            HttpServletRequest request
    ) {
        List<AuthorDto> authors = authorService.findAll().stream()
                .map(AuthorMapper::toDto)
                .toList();

        AuthorsXml payload = new AuthorsXml(authors);

        if (wantsXml(format, request)) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_XML)
                    .body(xml.toXmlWithXslt(payload, "/xsl/authors.xsl"));
        }
        return ResponseEntity.ok(payload);
    }

    @Operation(
            summary = "Получить автора по id",
            description = """
                    Возвращает автора.
                    - JSON: AuthorDto
                    - XML: строка XML + XSL processing-instruction (?format=xml)
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthorDto.class)),
                    @Content(mediaType = "application/xml",
                            schema = @Schema(type = "string", description = "XML строка с XSL processing-instruction"))
            }),
            @ApiResponse(responseCode = "404", description = "Автор не найден")
    })
    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> get(
            @PathVariable Long id,
            @Parameter(in = ParameterIn.QUERY, name = "format",
                    description = "Если указать format=xml — отдаём XML с XSL (для браузера)",
                    example = "xml")
            @RequestParam(value = "format", required = false) String format,
            HttpServletRequest request
    ) {
        Author author = authorService.getById(id);
        AuthorDto dto = AuthorMapper.toDto(author);

        if (wantsXml(format, request)) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_XML)
                    .body(xml.toXmlWithXslt(dto, "/xsl/author.xsl"));
        }
        return ResponseEntity.ok(dto);
    }

    @Operation(
            summary = "Книги автора",
            description = """
                    Возвращает список книг конкретного автора.
                    - JSON: объект-обёртка { "books": [...] } (BooksXml)
                    - XML: строка XML + XSL processing-instruction (?format=xml)
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BooksXml.class)),
                    @Content(mediaType = "application/xml",
                            schema = @Schema(type = "string", description = "XML строка с XSL processing-instruction"))
            }),
            @ApiResponse(responseCode = "404", description = "Автор не найден")
    })
    @GetMapping(value = "/{id}/books", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> booksOfAuthor(
            @PathVariable Long id,
            @Parameter(in = ParameterIn.QUERY, name = "format",
                    description = "Если указать format=xml — отдаём XML с XSL (для браузера)",
                    example = "xml")
            @RequestParam(value = "format", required = false) String format,
            HttpServletRequest request
    ) {
        // проверим что автор существует
        authorService.getById(id);

        List<BookDto> books = bookService.findAllByAuthorIdWithAuthor(id).stream()
                .map(BookMapper::toDto)
                .toList();

        BooksXml payload = new BooksXml(books);

        if (wantsXml(format, request)) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_XML)
                    .body(xml.toXmlWithXslt(payload, "/xsl/books.xsl"));
        }
        return ResponseEntity.ok(payload);
    }

    @Operation(
            summary = "Создать автора",
            description = "Принимает JSON или XML (AuthorUpsertDto). Возвращает созданного автора (AuthorDto)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created", content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthorDto.class)),
                    @Content(mediaType = "application/xml",
                            schema = @Schema(type = "string", description = "XML строка с XSL processing-instruction"))
            }),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации")
    })
    @PostMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public ResponseEntity<?> create(
            @Valid @RequestBody AuthorUpsertDto body,
            @Parameter(in = ParameterIn.QUERY, name = "format",
                    description = "Если указать format=xml — отдаём XML с XSL (для браузера)",
                    example = "xml")
            @RequestParam(value = "format", required = false) String format,
            HttpServletRequest request
    ) {
        Author toCreate = Author.builder()
                .fullName(body.getFullName())
                .birthYear(body.getBirthYear())
                .build();

        Author created = authorService.create(toCreate);
        AuthorDto dto = AuthorMapper.toDto(created);

        URI location = URI.create("/api/authors/" + created.getId());

        if (wantsXml(format, request)) {
            return ResponseEntity.created(location)
                    .contentType(MediaType.APPLICATION_XML)
                    .body(xml.toXmlWithXslt(dto, "/xsl/author.xsl"));
        }
        return ResponseEntity.created(location).body(dto);
    }

    @Operation(
            summary = "Обновить автора",
            description = "PUT /api/authors/{id}. Принимает AuthorUpsertDto. Возвращает обновлённого автора (AuthorDto)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthorDto.class)),
                    @Content(mediaType = "application/xml",
                            schema = @Schema(type = "string", description = "XML строка с XSL processing-instruction"))
            }),
            @ApiResponse(responseCode = "404", description = "Автор не найден"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации")
    })
    @PutMapping(
            value = "/{id}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @Valid @RequestBody AuthorUpsertDto body,
            @Parameter(in = ParameterIn.QUERY, name = "format",
                    description = "Если указать format=xml — отдаём XML с XSL (для браузера)",
                    example = "xml")
            @RequestParam(value = "format", required = false) String format,
            HttpServletRequest request
    ) {
        Author patch = Author.builder()
                .fullName(body.getFullName())
                .birthYear(body.getBirthYear())
                .build();

        Author updated = authorService.update(id, patch);
        AuthorDto dto = AuthorMapper.toDto(updated);

        if (wantsXml(format, request)) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_XML)
                    .body(xml.toXmlWithXslt(dto, "/xsl/author.xsl"));
        }
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Удалить автора", description = "Удаляет автора. Возвращает 204 No Content.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "404", description = "Автор не найден")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        authorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
