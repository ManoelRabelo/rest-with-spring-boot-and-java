package br.com.erudio.integrationtests.repositoies;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import br.com.erudio.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.erudio.model.Person;
import br.com.erudio.reposirories.PersonRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(OrderAnnotation.class)
public class PersonRepositoryTest extends AbstractIntegrationTest{

	@Autowired
	public PersonRepository repository;
	
	private static Person person;

	@BeforeAll
	public static void setup() {
		person = new Person();
	}
	
	@Test
	@Order(1)
	public void testFindByName() throws JsonMappingException, JsonProcessingException {
		
		Pageable pageable = PageRequest.of(0, 6, Sort.by(Direction.ASC, "firstName"));
		person = repository.findPersonsByName("yr", pageable).getContent().get(0);
		
		assertNotNull(person.getId());		
		assertNotNull(person.getFirstName());		
		assertNotNull(person.getLastName());		
		assertNotNull(person.getAddress());		
		assertNotNull(person.getGender());			
		
		assertTrue(person.getEnabled());	

		assertEquals(804, person.getId());
		
		assertEquals("Byram", person.getFirstName());		
		assertEquals("Birnie", person.getLastName());	
		assertEquals("58926 John Wall Terrace", person.getAddress());		
		assertEquals("Male", person.getGender());
	}

	@Test
	@Order(2)
	public void testDisablePerson() throws JsonMappingException, JsonProcessingException {
		
		repository.disablePerson(person.getId());
		
		Pageable pageable = PageRequest.of(0, 6, Sort.by(Direction.ASC, "firstName"));
		person = repository.findPersonsByName("yr", pageable).getContent().get(0);
		
		assertNotNull(person.getId());		
		assertNotNull(person.getFirstName());		
		assertNotNull(person.getLastName());		
		assertNotNull(person.getAddress());		
		assertNotNull(person.getGender());			
		
		assertFalse(person.getEnabled());	

		assertEquals(804, person.getId());
		
		assertEquals("Byram", person.getFirstName());		
		assertEquals("Birnie", person.getLastName());	
		assertEquals("58926 John Wall Terrace", person.getAddress());		
		assertEquals("Male", person.getGender());
	}

}
