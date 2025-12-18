package com.example.demo.web;

import com.example.demo.model.Author;
import com.example.demo.model.Book;
import com.example.demo.service.AuthorService;
import com.example.demo.service.BookService;
import com.example.demo.web.form.BookForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    private final AuthorService authorService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("books", bookService.findAllWithAuthor());
        return "books/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("bookForm", new BookForm());
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("pageTitle", "Добавить книгу");
        model.addAttribute("formAction", "/books");
        return "books/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("bookForm") BookForm form,
                         BindingResult bindingResult,
                         Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("authors", authorService.findAll());
            model.addAttribute("pageTitle", "Добавить книгу");
            model.addAttribute("formAction", "/books");
            return "books/form";
        }

        Author author = (form.getAuthorId() == null) ? null : authorService.getById(form.getAuthorId());

        Book book = Book.builder()
                .title(form.getTitle())
                .publishedYear(form.getPublishedYear())
                .author(author)
                .build();

        bookService.create(book);
        return "redirect:/books";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Book book = bookService.getByIdWithAuthor(id);

        BookForm form = new BookForm();
        form.setTitle(book.getTitle());
        form.setPublishedYear(book.getPublishedYear());
        form.setAuthorId(book.getAuthor() != null ? book.getAuthor().getId() : null);

        model.addAttribute("bookForm", form);
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("pageTitle", "Редактировать книгу");
        model.addAttribute("formAction", "/books/" + id);
        return "books/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("bookForm") BookForm form,
                         BindingResult bindingResult,
                         Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("authors", authorService.findAll());
            model.addAttribute("pageTitle", "Редактировать книгу");
            model.addAttribute("formAction", "/books/" + id);
            return "books/form";
        }

        Author author = (form.getAuthorId() == null) ? null : authorService.getById(form.getAuthorId());

        Book updated = Book.builder()
                .title(form.getTitle())
                .publishedYear(form.getPublishedYear())
                .author(author)
                .build();

        bookService.update(id, updated);
        return "redirect:/books";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        bookService.delete(id);
        return "redirect:/books";
    }
}
