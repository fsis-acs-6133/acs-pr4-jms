-- делаем author_id nullable + при удалении автора ставим NULL в books.author_id

ALTER TABLE books
    ALTER COLUMN author_id DROP NOT NULL;

ALTER TABLE books
    DROP CONSTRAINT IF EXISTS books_author_id_fkey;

ALTER TABLE books
    ADD CONSTRAINT books_author_id_fkey
        FOREIGN KEY (author_id)
        REFERENCES authors(id)
        ON DELETE SET NULL;
