package br.com.erudio.reposirories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.erudio.model.Book;

public interface BookRepository extends JpaRepository<Book, Long>{}
