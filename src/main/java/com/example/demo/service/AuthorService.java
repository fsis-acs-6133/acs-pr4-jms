package com.example.demo.service;

import com.example.demo.jms.ChangeType;
import com.example.demo.jms.EntityChangeMessage;
import com.example.demo.jms.EntityChangePublisher;
import com.example.demo.model.Author;
import com.example.demo.repository.AuthorRepository;
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
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final EntityChangePublisher changePublisher;

    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    public Author getById(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Автор не найден: id=" + id));
    }

    @Transactional
    public Author create(Author author) {
        Author saved = authorRepository.save(author);

        EntityChangeMessage event = new EntityChangeMessage(
                UUID.randomUUID().toString(),
                Instant.now(),
                ChangeType.INSERT,
                "Author",
                saved.getId(),
                null,
                snapshot(saved)
        );
        changePublisher.publishAfterCommit(event);

        return saved;
    }

    @Transactional
    public Author update(Long id, Author patch) {
        Author existing = getById(id);
        Map<String, Object> before = snapshot(existing);

        existing.setFullName(patch.getFullName());
        existing.setBirthYear(patch.getBirthYear());

        Author saved = authorRepository.save(existing);

        EntityChangeMessage event = new EntityChangeMessage(
                UUID.randomUUID().toString(),
                Instant.now(),
                ChangeType.UPDATE,
                "Author",
                saved.getId(),
                before,
                snapshot(saved)
        );
        changePublisher.publishAfterCommit(event);

        return saved;
    }

    @Transactional
    public void delete(Long id) {
        Author existing = getById(id);
        Map<String, Object> before = snapshot(existing);

        authorRepository.delete(existing);

        EntityChangeMessage event = new EntityChangeMessage(
                UUID.randomUUID().toString(),
                Instant.now(),
                ChangeType.DELETE,
                "Author",
                id,
                before,
                null
        );
        changePublisher.publishAfterCommit(event);
    }

    private Map<String, Object> snapshot(Author a) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", a.getId());
        m.put("fullName", a.getFullName());
        m.put("birthYear", a.getBirthYear());
        return m;
    }
}
