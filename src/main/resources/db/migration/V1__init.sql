DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS authors;

CREATE TABLE authors (
  id BIGSERIAL PRIMARY KEY,
  full_name VARCHAR(200) NOT NULL,
  birth_year INT
);

CREATE TABLE books (
  id BIGSERIAL PRIMARY KEY,
  title VARCHAR(300) NOT NULL,
  published_year INT,
  author_id BIGINT NOT NULL REFERENCES authors(id) ON DELETE RESTRICT
);

INSERT INTO authors(full_name, birth_year) VALUES
  ('Лев Толстой', 1828),
  ('Фёдор Достоевский', 1821);

INSERT INTO books(title, published_year, author_id) VALUES
  ('Война и мир', 1869, 1),
  ('Преступление и наказание', 1866, 2);
