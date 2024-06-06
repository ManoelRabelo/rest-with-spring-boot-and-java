package br.com.erudio.integrationtests.controller.withjson;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.erudio.configs.TestConfigs;
import br.com.erudio.data.vo.v1.security.TokenVO;
import br.com.erudio.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.erudio.integrationtests.vo.AccountCredentialsVO;
import br.com.erudio.integrationtests.vo.PersonVO;
import br.com.erudio.integrationtests.vo.wrappers.WrapperPersonVO;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class PersonControllerJsonTest extends AbstractIntegrationTest{

	private static RequestSpecification specification;
	private static ObjectMapper objectMapper;
	
	private static PersonVO person;
	
	@BeforeAll
	public static void setup() {
		objectMapper = new ObjectMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		
		person = new PersonVO();
	}
	
	@Test
	@Order(0)
	public void authorization() throws JsonMappingException, JsonProcessingException {
		AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");
		
		var accessToken = given()
				.basePath("/auth/signin")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.body(user)
					.when()
				.post()
					.then()
						.statusCode(200)
							.extract()
							.body()
								.as(TokenVO.class)
							.getAccessToken();
		
		specification = new RequestSpecBuilder()
				.addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION,"Bearer " + accessToken)
				.setBasePath("/api/person/v1")
				.setPort(TestConfigs.SERVER_PORT)
					.addFilter(new RequestLoggingFilter(LogDetail.ALL))
					.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
	}

	@Test
	@Order(1)
	public void testCreate() throws JsonMappingException, JsonProcessingException {
		mockPerson();
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.body(person)
				.when()
					.post()
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();
		
		PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
		person = persistedPerson;
		
		assertNotNull(persistedPerson);
		assertNotNull(persistedPerson.getId());		
		assertNotNull(persistedPerson.getFirstName());		
		assertNotNull(persistedPerson.getLastName());		
		assertNotNull(persistedPerson.getAddress());		
		assertNotNull(persistedPerson.getGender());
		
		assertTrue(persistedPerson.getEnabled());	
		assertTrue(persistedPerson.getId() > 0);
		
		assertEquals("Zoro", persistedPerson.getFirstName());		
		assertEquals("Roroa", persistedPerson.getLastName());		
		assertEquals("East Blue", persistedPerson.getAddress());		
		assertEquals("male", persistedPerson.getGender());		
	}

	@Test
	@Order(2)
	public void testUpdate() throws JsonMappingException, JsonProcessingException {
		person.setAddress("Wano");
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.body(person)
				.when()
					.put()
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();
		
		PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
		person = persistedPerson;
		
		assertNotNull(persistedPerson);
		assertNotNull(persistedPerson.getId());		
		assertNotNull(persistedPerson.getFirstName());		
		assertNotNull(persistedPerson.getLastName());		
		assertNotNull(persistedPerson.getAddress());		
		assertNotNull(persistedPerson.getGender());			
		assertTrue(persistedPerson.getEnabled());	
		
		assertEquals(person.getId(), persistedPerson.getId());
		
		assertEquals("Zoro", persistedPerson.getFirstName());		
		assertEquals("Roroa", persistedPerson.getLastName());		
		assertEquals("Wano", persistedPerson.getAddress());		
		assertEquals("male", persistedPerson.getGender());		
	}

	@Test
	@Order(3)
	public void testDisablePersonById() throws JsonMappingException, JsonProcessingException {

		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.pathParam("id", person.getId())
					.when()
					.patch("{id}")
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();
		
		PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
		person = persistedPerson;
		
		assertNotNull(persistedPerson);
		assertNotNull(persistedPerson.getId());		
		assertNotNull(persistedPerson.getFirstName());		
		assertNotNull(persistedPerson.getLastName());		
		assertNotNull(persistedPerson.getAddress());		
		assertNotNull(persistedPerson.getGender());		
		assertFalse(persistedPerson.getEnabled());		
		
		assertEquals(person.getId(), persistedPerson.getId());
		
		assertEquals("Zoro", persistedPerson.getFirstName());		
		assertEquals("Roroa", persistedPerson.getLastName());		
		assertEquals("Wano", persistedPerson.getAddress());		
		assertEquals("male", persistedPerson.getGender());		
	}

	@Test
	@Order(4)
	public void testFindById() throws JsonMappingException, JsonProcessingException {
		mockPerson();

		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.pathParam("id", person.getId())
					.when()
					.get("{id}")
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();
		
		PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
		person = persistedPerson;
		
		assertNotNull(persistedPerson);
		assertNotNull(persistedPerson.getId());		
		assertNotNull(persistedPerson.getFirstName());		
		assertNotNull(persistedPerson.getLastName());		
		assertNotNull(persistedPerson.getAddress());		
		assertNotNull(persistedPerson.getGender());		
		assertFalse(persistedPerson.getEnabled());		
		
		assertEquals(person.getId(), persistedPerson.getId());
		
		assertEquals("Zoro", persistedPerson.getFirstName());		
		assertEquals("Roroa", persistedPerson.getLastName());		
		assertEquals("Wano", persistedPerson.getAddress());		
		assertEquals("male", persistedPerson.getGender());		
	}
	
	@Test
	@Order(5)
	public void testDelete() throws JsonMappingException, JsonProcessingException {
		
		given().spec(specification)
			.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.pathParam("id", person.getId())
			.when()
				.delete("{id}")
			.then()
				.statusCode(204);	
	}

	@Test
	@Order(6)
	public void testFindAll() throws JsonMappingException, JsonProcessingException {
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.queryParams("page", 3, "size", 10, "direction", "asc")
				.when()
					.get()
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();
		
		WrapperPersonVO wrapper = objectMapper.readValue(content, WrapperPersonVO.class);
		var people = wrapper.getEmbedded().getPersons();
		
		PersonVO foundPersonOne = people.get(0);
		
		assertNotNull(foundPersonOne.getId());		
		assertNotNull(foundPersonOne.getFirstName());		
		assertNotNull(foundPersonOne.getLastName());		
		assertNotNull(foundPersonOne.getAddress());		
		assertNotNull(foundPersonOne.getGender());			
		
		assertTrue(foundPersonOne.getEnabled());	

		assertEquals(20, foundPersonOne.getId());
		
		assertEquals("Alyssa", foundPersonOne.getFirstName());		
		assertEquals("Rowthorne", foundPersonOne.getLastName());	
		assertEquals("67195 Rusk Pass", foundPersonOne.getAddress());		
		assertEquals("Female", foundPersonOne.getGender());		

		PersonVO foundPersonSix = people.get(5);

		assertNotNull(foundPersonSix.getId());		
		assertNotNull(foundPersonSix.getFirstName());		
		assertNotNull(foundPersonSix.getLastName());		
		assertNotNull(foundPersonSix.getAddress());		
		assertNotNull(foundPersonSix.getGender());		
		
		assertFalse(foundPersonSix.getEnabled());	

		assertEquals(790, foundPersonSix.getId());
		
		assertEquals("Anastasia", foundPersonSix.getFirstName());		
		assertEquals("Deverock", foundPersonSix.getLastName());		
		assertEquals("67616 Bayside Park", foundPersonSix.getAddress());		
		assertEquals("Female", foundPersonSix.getGender());		
	}

	@Test
	@Order(7)
	public void testFindAllWithoutToken() throws JsonMappingException, JsonProcessingException {
		
		RequestSpecification specificationWithoutToken= new RequestSpecBuilder()
				.setBasePath("/api/person/v1")
				.setPort(TestConfigs.SERVER_PORT)
					.addFilter(new RequestLoggingFilter(LogDetail.ALL))
					.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
		
		given().spec(specificationWithoutToken)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.when()
					.get()
				.then()
					.statusCode(403);
	}
	
	private void mockPerson() {
		person.setFirstName("Zoro");
		person.setLastName("Roroa");
		person.setAddress("East Blue");
		person.setGender("male");
		person.setEnabled(true);
	}

	@Test
	@Order(8)
	public void testFindByName() throws JsonMappingException, JsonProcessingException {
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.pathParam("firstName", "ayr")
				.queryParams("page", 0, "size", 6, "direction", "asc")
					.when()
					.get("findPersonByName/{firstName}")
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();
		
		WrapperPersonVO wrapper = objectMapper.readValue(content, WrapperPersonVO.class);
		var people = wrapper.getEmbedded().getPersons();
		
		PersonVO foundPersonOne = people.get(0);
		
		assertNotNull(foundPersonOne.getId());		
		assertNotNull(foundPersonOne.getFirstName());		
		assertNotNull(foundPersonOne.getLastName());		
		assertNotNull(foundPersonOne.getAddress());		
		assertNotNull(foundPersonOne.getGender());			
		
		assertFalse(foundPersonOne.getEnabled());	

		assertEquals(111, foundPersonOne.getId());
		
		assertEquals("Fayre", foundPersonOne.getFirstName());		
		assertEquals("Griffitt", foundPersonOne.getLastName());	
		assertEquals("355 Grasskamp Place", foundPersonOne.getAddress());		
		assertEquals("Female", foundPersonOne.getGender());
	}
	
	@Test
	@Order(9)
	public void testHATEOAS() throws JsonMappingException, JsonProcessingException {
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.queryParams("page", 3, "size", 10, "direction", "asc")
				.when()
					.get()
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();

		assertTrue(content.contains
			("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/person/v1/20\"}}},"));
		assertTrue(content.contains
			("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/person/v1/495\"}}},"));
		assertTrue(content.contains
			("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/person/v1/161\"}}},"));

		assertTrue(content.contains
			("{\"first\":{\"href\":\"http://localhost:8888/api/person/v1?direction=asc&page=0&size=10&sort=firstName,asc\"},"));
		assertTrue(content.contains
			("\"prev\":{\"href\":\"http://localhost:8888/api/person/v1?direction=asc&page=2&size=10&sort=firstName,asc\"},"));
		assertTrue(content.contains
			("\"self\":{\"href\":\"http://localhost:8888/api/person/v1?page=3&size=10&direction=asc\"},"));
		assertTrue(content.contains
			("\"next\":{\"href\":\"http://localhost:8888/api/person/v1?direction=asc&page=4&size=10&sort=firstName,asc\"},"));
		assertTrue(content.contains
			("\"last\":{\"href\":\"http://localhost:8888/api/person/v1?direction=asc&page=100&size=10&sort=firstName,asc\"}}"));

		assertTrue(content.contains
			("\"page\":{\"size\":10,\"totalElements\":1009,\"totalPages\":101,\"number\":3}}"));
	}

}