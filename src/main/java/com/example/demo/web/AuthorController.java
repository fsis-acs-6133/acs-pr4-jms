package com.example.demo.web;

import com.example.demo.model.Author;
import com.example.demo.service.AuthorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/authors")
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("authors", authorService.findAll());
        return "authors/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("author", new Author());
        model.addAttribute("pageTitle", "Добавить автора");
        model.addAttribute("formAction", "/authors");
        return "authors/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("author") Author author,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Добавить автора");
            model.addAttribute("formAction", "/authors");
            return "authors/form";
        }
        authorService.create(author);
        return "redirect:/authors";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("author", authorService.getById(id));
        model.addAttribute("pageTitle", "Редактировать автора");
        model.addAttribute("formAction", "/authors/" + id);
        return "authors/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("author") Author author,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Редактировать автора");
            model.addAttribute("formAction", "/authors/" + id);
            return "authors/form";
        }
        authorService.update(id, author);
        return "redirect:/authors";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        authorService.delete(id);
        return "redirect:/authors";
    }
}
