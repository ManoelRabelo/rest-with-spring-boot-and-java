package br.com.semeru.reposirories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.semeru.model.Book;

public interface BookRepository extends JpaRepository<Book, Long>{}
