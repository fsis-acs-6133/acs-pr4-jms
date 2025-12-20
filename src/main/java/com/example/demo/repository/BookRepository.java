package com.example.demo.repository;

import com.example.demo.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("select b from Book b left join fetch b.author")
    List<Book> findAllWithAuthor();

    @Query("select b from Book b left join fetch b.author where b.id = :id")
    Optional<Book> findByIdWithAuthor(@Param("id") Long id);

    @Query("select b from Book b left join fetch b.author where b.author.id = :authorId")
    List<Book> findAllByAuthorIdWithAuthor(@Param("authorId") Long authorId);
}
