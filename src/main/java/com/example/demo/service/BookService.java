package com.example.demo.service;

import com.example.demo.jms.ChangeType;
import com.example.demo.jms.EntityChangeMessage;
import com.example.demo.jms.EntityChangePublisher;
import com.example.demo.model.Book;
import com.example.demo.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final EntityChangePublisher changePublisher;

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public List<Book> findAllWithAuthor() {
        return bookRepository.findAllWithAuthor();
    }

    public List<Book> findAllByAuthorIdWithAuthor(Long authorId) {
        return bookRepository.findAllByAuthorIdWithAuthor(authorId);
    }

    public Book getById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Книга не найдена: id=" + id));
    }

    public Book getByIdWithAuthor(Long id) {
        return bookRepository.findByIdWithAuthor(id)
                .orElseThrow(() -> new IllegalArgumentException("Книга не найдена: id=" + id));
    }

    @Transactional
    public Book create(Book book) {
        Book saved = bookRepository.save(book);

        EntityChangeMessage event = new EntityChangeMessage(
                UUID.randomUUID().toString(),
                Instant.now(),
                ChangeType.INSERT,
                "Book",
                saved.getId(),
                null,
                snapshot(saved)
        );
        changePublisher.publishAfterCommit(event);

        return saved;
    }

    @Transactional
    public Book update(Long id, Book patch) {
        Book existing = getById(id);
        Map<String, Object> before = snapshot(existing);

        existing.setTitle(patch.getTitle());
        existing.setPublishedYear(patch.getPublishedYear());
        existing.setAuthor(patch.getAuthor()); // допускается null, если author_id nullable

        Book saved = bookRepository.save(existing);

        EntityChangeMessage event = new EntityChangeMessage(
                UUID.randomUUID().toString(),
                Instant.now(),
                ChangeType.UPDATE,
                "Book",
                saved.getId(),
                before,
                snapshot(saved)
        );
        changePublisher.publishAfterCommit(event);

        return saved;
    }

    @Transactional
    public void delete(Long id) {
        Book existing = getById(id);
        Map<String, Object> before = snapshot(existing);

        bookRepository.delete(existing);

        EntityChangeMessage event = new EntityChangeMessage(
                UUID.randomUUID().toString(),
                Instant.now(),
                ChangeType.DELETE,
                "Book",
                id,
                before,
                null
        );
        changePublisher.publishAfterCommit(event);
    }

    private Map<String, Object> snapshot(Book b) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", b.getId());
        m.put("title", b.getTitle());
        m.put("publishedYear", b.getPublishedYear());
        m.put("authorId", b.getAuthor() != null ? b.getAuthor().getId() : null);
        return m;
    }
}
