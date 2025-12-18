package com.example.demo.service;

import com.example.demo.model.Author;
import com.example.demo.repository.AuthorRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthorService {

    private final AuthorRepository authorRepository;

    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    public Author getById(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Автор не найден: id=" + id));
    }

    @Transactional
    public Author create(Author author) {
        author.setId(null);
        return authorRepository.save(author);
    }

    @Transactional
    public Author update(Long id, Author updated) {
        Author existing = getById(id);
        existing.setFullName(updated.getFullName());
        existing.setBirthYear(updated.getBirthYear());
        return existing;
    }

    @Transactional
    public void delete(Long id) {
        // При ON DELETE SET NULL книги останутся, а author_id станет NULL
        if (!authorRepository.existsById(id)) {
            throw new EntityNotFoundException("Автор не найден: id=" + id);
        }
        authorRepository.deleteById(id);
    }
}
