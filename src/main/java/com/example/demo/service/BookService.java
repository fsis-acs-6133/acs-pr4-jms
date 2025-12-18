package com.example.demo.service;

import com.example.demo.model.Book;
import com.example.demo.repository.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;

    public List<Book> findAllWithAuthor() {
        return bookRepository.findAllWithAuthor();
    }

    public Book getByIdWithAuthor(Long id) {
        return bookRepository.findByIdWithAuthor(id)
                .orElseThrow(() -> new EntityNotFoundException("Книга не найдена: id=" + id));
    }

    @Transactional
    public Book create(Book book) {
        book.setId(null);
        return bookRepository.save(book);
    }

    @Transactional
    public Book update(Long id, Book updated) {
        Book existing = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Книга не найдена: id=" + id));

        existing.setTitle(updated.getTitle());
        existing.setPublishedYear(updated.getPublishedYear());
        existing.setAuthor(updated.getAuthor()); // может быть null

        return existing;
    }

    @Transactional
    public void delete(Long id) {
        bookRepository.deleteById(id);
    }
}
