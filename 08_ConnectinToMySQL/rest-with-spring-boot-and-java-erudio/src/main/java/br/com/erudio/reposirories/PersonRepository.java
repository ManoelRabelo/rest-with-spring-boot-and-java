package br.com.erudio.reposirories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.erudio.model.Person;

public interface PersonRepository extends JpaRepository<Person, Long>{}
