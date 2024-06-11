package br.com.erudio.integrationtests.controller.withxml;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import br.com.erudio.configs.TestConfigs;
import br.com.erudio.data.vo.v1.security.TokenVO;
import br.com.erudio.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.erudio.integrationtests.vo.AccountCredentialsVO;
import br.com.erudio.integrationtests.vo.BookVO;
import br.com.erudio.integrationtests.vo.pagedmodels.PagedModelBook;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class BookControllerXmlTest extends AbstractIntegrationTest{

	private static RequestSpecification specification;
	private static XmlMapper objectMapper;
	
	private static BookVO book;
	
	@BeforeAll
	public static void setup() {
		objectMapper = new XmlMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		
		book = new BookVO();
	}
	
	@Test
	@Order(0)
	public void authorization() throws JsonMappingException, JsonProcessingException {
		AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");
		
		var accessToken = given()
				.basePath("/auth/signin")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_XML)
					.accept(TestConfigs.CONTENT_TYPE_XML)
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
				.setBasePath("/api/book/v1")
				.setPort(TestConfigs.SERVER_PORT)
					.addFilter(new RequestLoggingFilter(LogDetail.ALL))
					.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
	}

	@Test
	@Order(1)
	public void testCreate() throws JsonMappingException, JsonProcessingException {
		MockBook();
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
					.body(book)
				.when()
					.post()
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();
		
		BookVO persistedBook = objectMapper.readValue(content, BookVO.class);
		book = persistedBook;
		
		assertNotNull(persistedBook);
		assertNotNull(persistedBook.getId());		
		assertNotNull(persistedBook.getAuthor());		
		assertNotNull(persistedBook.getTitle());		
		assertNotNull(persistedBook.getLaunchDate());		
		assertNotNull(persistedBook.getPrice());		
		
		assertTrue(persistedBook.getId() > 0);
		
		assertEquals("Eiichiro Oda", persistedBook.getAuthor());		
		assertEquals("One Piece", persistedBook.getTitle());		
		assertEquals(19.99, persistedBook.getPrice());		
	}

	@Test
	@Order(2)
	public void testUpdate() throws JsonMappingException, JsonProcessingException {
		book.setPrice(9.99);
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
					.body(book)
				.when()
					.put()
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();
		
		BookVO persistedBook = objectMapper.readValue(content, BookVO.class);
		book = persistedBook;
		
		assertNotNull(persistedBook);
		assertNotNull(persistedBook.getId());		
		assertNotNull(persistedBook.getAuthor());		
		assertNotNull(persistedBook.getTitle());		
		assertNotNull(persistedBook.getLaunchDate());		
		assertNotNull(persistedBook.getPrice());		
		
		assertEquals(book.getId(), persistedBook.getId());
		
		assertEquals("Eiichiro Oda", persistedBook.getAuthor());		
		assertEquals("One Piece", persistedBook.getTitle());		
		assertEquals(9.99, persistedBook.getPrice());		
	}

	@Test
	@Order(3)
	public void testFindById() throws JsonMappingException, JsonProcessingException {
		MockBook();

		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
					.pathParam("id", book.getId())
					.when()
					.get("{id}")
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();
		
		BookVO persistedBook = objectMapper.readValue(content, BookVO.class);
		book = persistedBook;
		
		assertNotNull(persistedBook);
		assertNotNull(persistedBook.getId());		
		assertNotNull(persistedBook.getAuthor());		
		assertNotNull(persistedBook.getTitle());		
		assertNotNull(persistedBook.getLaunchDate());		
		assertNotNull(persistedBook.getPrice());		
		
		assertTrue(persistedBook.getId() > 0);
		
		assertEquals("Eiichiro Oda", persistedBook.getAuthor());		
		assertEquals("One Piece", persistedBook.getTitle());		
		assertEquals(9.99, persistedBook.getPrice());		
	}
	
	@Test
	@Order(4)
	public void testDelete() throws JsonMappingException, JsonProcessingException {
		
		given().spec(specification)
		.contentType(TestConfigs.CONTENT_TYPE_XML)
		.accept(TestConfigs.CONTENT_TYPE_XML)	
				.pathParam("id", book.getId())
			.when()
				.delete("{id}")
			.then()
				.statusCode(204);	
	}

	@Test
	@Order(5)
	public void testFindAll() throws JsonMappingException, JsonProcessingException {
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
				.queryParams("page", 0, "size", 5, "direction", "asc")
				.when()
					.get()
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();
		PagedModelBook wrapper = objectMapper.readValue(content, PagedModelBook.class);
		var books = wrapper.getContent();

		BookVO foundBookOne = books.get(0);
		
		assertNotNull(foundBookOne.getId());		
		assertNotNull(foundBookOne.getAuthor());		
		assertNotNull(foundBookOne.getTitle());		
		assertNotNull(foundBookOne.getLaunchDate());		
		assertNotNull(foundBookOne.getPrice());		
		
		assertEquals(6, foundBookOne.getId());		
		assertEquals("R. F. Kuang", foundBookOne.getAuthor());		
		assertEquals("A guerra da papoula", foundBookOne.getTitle());		
		assertEquals(47.99, foundBookOne.getPrice());		

		BookVO foundBookFive = books.get(4);

		assertNotNull(foundBookFive.getId());		
		assertNotNull(foundBookFive.getAuthor());		
		assertNotNull(foundBookFive.getTitle());		
		assertNotNull(foundBookFive.getLaunchDate());		
		assertNotNull(foundBookFive.getPrice());		
		
		assertEquals(5, foundBookFive.getId());		
		assertEquals("Yoshihiro Togashi", foundBookFive.getAuthor());		
		assertEquals("Hunter x Hunter", foundBookFive.getTitle());		
		assertEquals(30.00, foundBookFive.getPrice());		
	}

	@Test
	@Order(6)
	public void testFindAllWithoutToken() throws JsonMappingException, JsonProcessingException {
		
		RequestSpecification specificationWithoutToken= new RequestSpecBuilder()
				.setBasePath("/api/book/v1")
				.setPort(TestConfigs.SERVER_PORT)
					.addFilter(new RequestLoggingFilter(LogDetail.ALL))
					.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
		
		given().spec(specificationWithoutToken)
		.contentType(TestConfigs.CONTENT_TYPE_XML)
		.accept(TestConfigs.CONTENT_TYPE_XML)
				.when()
					.get()
				.then()
					.statusCode(403);
	}
	
	private void MockBook() {
		book.setAuthor("Eiichiro Oda");
		book.setTitle("One Piece");
		book.setPrice(19.99);
		book.setLaunchDate(new Date());
	}

	@Test
	@Order(5)
	public void testHATEOAS() throws JsonMappingException, JsonProcessingException {
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
				.queryParams("page", 1, "size", 3, "direction", "asc")
				.when()
					.get()
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();
		
		assertTrue(content.contains
			("<links><rel>self</rel><href>http://localhost:8888/api/book/v1/3</href></links>"));
		assertTrue(content.contains
			("<links><rel>self</rel><href>http://localhost:8888/api/book/v1/5</href></links>"));
		assertTrue(content.contains
			("<links><rel>self</rel><href>http://localhost:8888/api/book/v1/4</href></links>"));
		
		assertTrue(content.contains
			("<links><rel>first</rel><href>http://localhost:8888/api/person/v1?direction=asc&amp;page=0&amp;size=3&amp;sort=title,asc</href></links>"));
		assertTrue(content.contains
			("<links><rel>prev</rel><href>http://localhost:8888/api/person/v1?direction=asc&amp;page=0&amp;size=3&amp;sort=title,asc</href></links>"));
		assertTrue(content.contains
			("<links><rel>self</rel><href>http://localhost:8888/api/person/v1?page=1&amp;size=3&amp;direction=asc</href></links>"));
		assertTrue(content.contains
			("<links><rel>next</rel><href>http://localhost:8888/api/person/v1?direction=asc&amp;page=2&amp;size=3&amp;sort=title,asc</href></links>"));
		assertTrue(content.contains
			("<links><rel>last</rel><href>http://localhost:8888/api/person/v1?direction=asc&amp;page=2&amp;size=3&amp;sort=title,asc</href></links>"));

		assertTrue(content.contains
			("<page><size>3</size><totalElements>8</totalElements><totalPages>3</totalPages><number>1</number></page>"));

	}
}