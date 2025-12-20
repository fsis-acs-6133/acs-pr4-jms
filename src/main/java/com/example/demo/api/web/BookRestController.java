package com.example.demo.api.web;

import com.example.demo.api.dto.BookDto;
import com.example.demo.api.dto.BookUpsertDto;
import com.example.demo.api.dto.BooksXml;
import com.example.demo.api.mapper.BookMapper;
import com.example.demo.api.support.XmlWithXsltService;
import com.example.demo.model.Author;
import com.example.demo.model.Book;
import com.example.demo.service.AuthorService;
import com.example.demo.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
@Tag(name = "Books REST", description = "CRUD для книг. JSON и XML поддерживаются.")
public class BookRestController {

    private final BookService bookService;
    private final AuthorService authorService;
    private final XmlWithXsltService xml;

    private boolean wantsXml(String format, HttpServletRequest request) {
        if (format != null && format.equalsIgnoreCase("xml")) return true;

        String accept = request.getHeader("Accept");
        return accept != null && accept.toLowerCase().contains("xml");
    }

    @Operation(
            summary = "Список книг",
            description = """
                    Возвращает список книг вместе с автором.
                    - JSON: объект-обёртка { "books": [...] } (BooksXml)
                    - XML: строка XML + xml-stylesheet PI для отображения в браузере (?format=xml)
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BooksXml.class)),
                    @Content(mediaType = "application/xml", schema = @Schema(
                            type = "string",
                            description = "XML строка с XSL processing-instruction"
                    ))
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
        List<BookDto> books = bookService.findAllWithAuthor().stream()
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
            summary = "Получить книгу по id",
            description = """
                    Возвращает книгу (включая автора).
                    - JSON: BookDto
                    - XML: строка XML + XSL processing-instruction (?format=xml)
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BookDto.class)),
                    @Content(mediaType = "application/xml", schema = @Schema(
                            type = "string",
                            description = "XML строка с XSL processing-instruction"
                    ))
            }),
            @ApiResponse(responseCode = "404", description = "Книга не найдена")
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
        Book book = bookService.getByIdWithAuthor(id);
        BookDto dto = BookMapper.toDto(book);

        if (wantsXml(format, request)) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_XML)
                    .body(xml.toXmlWithXslt(dto, "/xsl/book.xsl"));
        }
        return ResponseEntity.ok(dto);
    }

    @Operation(
            summary = "Создать книгу",
            description = "Принимает JSON или XML (BookUpsertDto). Возвращает созданную книгу (BookDto)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BookDto.class)),
                    @Content(mediaType = "application/xml", schema = @Schema(
                            type = "string",
                            description = "XML строка с XSL processing-instruction"
                    ))
            }),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
            @ApiResponse(responseCode = "404", description = "Автор не найден (authorId)")
    })
    @PostMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public ResponseEntity<?> create(
            @Valid @RequestBody BookUpsertDto body,
            @Parameter(in = ParameterIn.QUERY, name = "format",
                    description = "Если указать format=xml — отдаём XML с XSL (для браузера)",
                    example = "xml")
            @RequestParam(value = "format", required = false) String format,
            HttpServletRequest request
    ) {
        Author author = authorService.getById(body.getAuthorId());

        Book toCreate = Book.builder()
                .title(body.getTitle())
                .publishedYear(body.getPublishedYear())
                .author(author)
                .build();

        Book created = bookService.create(toCreate);

        // Чтобы в ответе точно был автор 
        BookDto dto = BookMapper.toDto(bookService.getByIdWithAuthor(created.getId()));

        URI location = URI.create("/api/books/" + created.getId());

        if (wantsXml(format, request)) {
            return ResponseEntity.created(location)
                    .contentType(MediaType.APPLICATION_XML)
                    .body(xml.toXmlWithXslt(dto, "/xsl/book.xsl"));
        }
        return ResponseEntity.created(location).body(dto);
    }

    @Operation(
            summary = "Обновить книгу",
            description = "PUT /api/books/{id}. Принимает BookUpsertDto. Возвращает обновлённую книгу (BookDto)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BookDto.class)),
                    @Content(mediaType = "application/xml", schema = @Schema(
                            type = "string",
                            description = "XML строка с XSL processing-instruction"
                    ))
            }),
            @ApiResponse(responseCode = "404", description = "Книга или автор не найдены"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации")
    })
    @PutMapping(
            value = "/{id}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @Valid @RequestBody BookUpsertDto body,
            @Parameter(in = ParameterIn.QUERY, name = "format",
                    description = "Если указать format=xml — отдаём XML с XSL (для браузера)",
                    example = "xml")
            @RequestParam(value = "format", required = false) String format,
            HttpServletRequest request
    ) {
        Author author = authorService.getById(body.getAuthorId());

        Book patch = Book.builder()
                .title(body.getTitle())
                .publishedYear(body.getPublishedYear())
                .author(author)
                .build();

        bookService.update(id, patch);

        BookDto dto = BookMapper.toDto(bookService.getByIdWithAuthor(id));

        if (wantsXml(format, request)) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_XML)
                    .body(xml.toXmlWithXslt(dto, "/xsl/book.xsl"));
        }
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Удалить книгу", description = "Удаляет книгу. Возвращает 204 No Content.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "404", description = "Книга не найдена")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
