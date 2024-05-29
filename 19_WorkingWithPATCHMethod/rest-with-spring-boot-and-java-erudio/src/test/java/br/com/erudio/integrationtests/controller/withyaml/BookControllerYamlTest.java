package br.com.erudio.integrationtests.controller.withyaml;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import br.com.erudio.configs.TestConfigs;
import br.com.erudio.data.vo.v1.security.TokenVO;
import br.com.erudio.integrationtests.controller.withyaml.mapper.YMLMapper;
import br.com.erudio.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.erudio.integrationtests.vo.AccountCredentialsVO;
import br.com.erudio.integrationtests.vo.BookVO;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class BookControllerYamlTest extends AbstractIntegrationTest{

	private static RequestSpecification specification;
	private static YMLMapper objectMapper;
	
	private static BookVO book;
	
	@BeforeAll
	public static void setup() {
		objectMapper = new YMLMapper();
		
		book = new BookVO();
	}
	
	@Test
	@Order(0)
	public void authorization() throws JsonMappingException, JsonProcessingException {
		AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");
		
		var accessToken = given()
				.config(
						RestAssuredConfig
							.config()
							.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(
									TestConfigs.CONTENT_TYPE_YML,
									ContentType.TEXT)))
				.basePath("/auth/signin")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_YML)
					.accept(TestConfigs.CONTENT_TYPE_YML)
				.body(user, objectMapper)
					.when()
				.post()
					.then()
						.statusCode(200)
							.extract()
							.body()
								.as(TokenVO.class, objectMapper)
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
		
		var persistedBook = given().spec(specification)
				.config(
						RestAssuredConfig
							.config()
							.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(
									TestConfigs.CONTENT_TYPE_YML,
									ContentType.TEXT)))
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.accept(TestConfigs.CONTENT_TYPE_YML)
					.body(book, objectMapper)
				.when()
					.post()
				.then()
					.statusCode(200)
						.extract()
						.body()
							.as(BookVO.class, objectMapper);
		
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
		
		var persistedBook = given().spec(specification)
				.config(
						RestAssuredConfig
							.config()
							.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(
									TestConfigs.CONTENT_TYPE_YML,
									ContentType.TEXT)))
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.accept(TestConfigs.CONTENT_TYPE_YML)
					.body(book, objectMapper)
				.when()
					.put()
				.then()
					.statusCode(200)
						.extract()
						.body()
							.as(BookVO.class, objectMapper);
		
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

		var persistedBook = given().spec(specification)
				.config(
						RestAssuredConfig
							.config()
							.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(
									TestConfigs.CONTENT_TYPE_YML,
									ContentType.TEXT)))
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.accept(TestConfigs.CONTENT_TYPE_YML)
					.pathParam("id", book.getId())
					.when()
					.get("{id}")
				.then()
					.statusCode(200)
						.extract()
						.body()
							.as(BookVO.class, objectMapper);
		
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
		.config(
				RestAssuredConfig
					.config()
					.encoderConfig(EncoderConfig.encoderConfig()
						.encodeContentTypeAs(
							TestConfigs.CONTENT_TYPE_YML,
							ContentType.TEXT)))
		.contentType(TestConfigs.CONTENT_TYPE_YML)
		.accept(TestConfigs.CONTENT_TYPE_YML)	
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
				.config(
						RestAssuredConfig
							.config()
							.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(
									TestConfigs.CONTENT_TYPE_YML,
									ContentType.TEXT)))
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.accept(TestConfigs.CONTENT_TYPE_YML)
				.when()
					.get()
				.then()
					.statusCode(200)
						.extract()
						.body()
							.as(BookVO[].class, objectMapper);
		
		List<BookVO> people = Arrays.asList(content);

		BookVO foundBookOne = people.get(0);
		
		assertNotNull(foundBookOne.getId());		
		assertNotNull(foundBookOne.getAuthor());		
		assertNotNull(foundBookOne.getTitle());		
		assertNotNull(foundBookOne.getLaunchDate());		
		assertNotNull(foundBookOne.getPrice());		
		
		assertEquals(1, foundBookOne.getId());		
		assertEquals("Eiichiro Oda", foundBookOne.getAuthor());		
		assertEquals("One piece", foundBookOne.getTitle());		
		assertEquals(19.99, foundBookOne.getPrice());		

		BookVO foundBooFive = people.get(4);

		assertNotNull(foundBooFive.getId());		
		assertNotNull(foundBooFive.getAuthor());		
		assertNotNull(foundBooFive.getTitle());		
		assertNotNull(foundBooFive.getLaunchDate());		
		assertNotNull(foundBooFive.getPrice());		
		
		assertEquals(5, foundBooFive.getId());		
		assertEquals("Yoshihiro Togashi", foundBooFive.getAuthor());		
		assertEquals("Hunter x Hunter", foundBooFive.getTitle());		
		assertEquals(30.00, foundBooFive.getPrice());		
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
			.config(
				RestAssuredConfig
				.config()
				.encoderConfig(EncoderConfig.encoderConfig()
					.encodeContentTypeAs(
						TestConfigs.CONTENT_TYPE_YML,
						ContentType.TEXT)))
		.contentType(TestConfigs.CONTENT_TYPE_YML)
		.accept(TestConfigs.CONTENT_TYPE_YML)
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

}