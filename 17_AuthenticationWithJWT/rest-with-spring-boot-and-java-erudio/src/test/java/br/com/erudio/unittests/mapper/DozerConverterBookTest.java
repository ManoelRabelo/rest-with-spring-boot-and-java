package br.com.erudio.unittests.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.erudio.data.vo.v1.BookVO;
import br.com.erudio.mapper.DozerMapper;
import br.com.erudio.model.Book;
import br.com.erudio.unittests.mapper.mocks.MockBook;

public class DozerConverterBookTest {
	MockBook inputObject;

	@BeforeEach
	public void setUp() {
		inputObject = new MockBook();
	}

	@Test
	public void parseEntityToVOTest() {
		BookVO output = DozerMapper.parseObject(inputObject.mockEntity(), BookVO.class);
		assertEquals(Long.valueOf(0L), output.getKey());
		assertEquals("Author Name0", output.getAuthor());
		assertEquals(5.0, output.getPrice());
		assertEquals("Title Name0", output.getTitle());
		assertNotNull(output.getLaunchDate());
	}

	@Test
	public void parseEntityListToVOListTest() {
		List<BookVO> outputList = DozerMapper.parseListObjects(inputObject.mockEntityList(), BookVO.class);
		
		BookVO outputZero = outputList.get(0);

		assertEquals(Long.valueOf(0L), outputZero.getKey());
		assertEquals("Author Name0", outputZero.getAuthor());
		assertEquals(5.0, outputZero.getPrice());
		assertEquals("Title Name0", outputZero.getTitle());
		assertNotNull(outputZero.getLaunchDate());

		BookVO outputSeven = outputList.get(7);

		assertEquals(Long.valueOf(7L), outputSeven.getKey());
		assertEquals("Author Name7", outputSeven.getAuthor());
		assertEquals(40.0, outputSeven.getPrice());
		assertEquals("Title Name7", outputSeven.getTitle());
		assertNotNull(outputSeven.getLaunchDate());

		BookVO outputTwelve = outputList.get(12);

		assertEquals(Long.valueOf(12L), outputTwelve.getKey());
		assertEquals("Author Name12", outputTwelve.getAuthor());
		assertEquals(65.0, outputTwelve.getPrice());
		assertEquals("Title Name12", outputTwelve.getTitle());
		assertNotNull(outputTwelve.getLaunchDate());
	}

	@Test
	public void parseVOToEntityTest() {
		Book output = DozerMapper.parseObject(inputObject.mockVO(), Book.class);
		assertEquals(Long.valueOf(0L), output.getId());
		assertEquals("Author Name0", output.getAuthor());
		assertEquals(5.0, output.getPrice());
		assertEquals("Title Name0", output.getTitle());
		assertNotNull(output.getLaunchDate());
	}

	@Test
	public void parserVOListToEntityListTest() {
		List<Book> outputList = DozerMapper.parseListObjects(inputObject.mockEntityList(), Book.class);
		
		Book outputZero = outputList.get(0);

		assertEquals(Long.valueOf(0L), outputZero.getId());
		assertEquals("Author Name0", outputZero.getAuthor());
		assertEquals(5.0, outputZero.getPrice());
		assertEquals("Title Name0", outputZero.getTitle());
		assertNotNull(outputZero.getLaunchDate());

		Book outputSeven = outputList.get(7);

		assertEquals(Long.valueOf(7L), outputSeven.getId());
		assertEquals("Author Name7", outputSeven.getAuthor());
		assertEquals(40.0, outputSeven.getPrice());
		assertEquals("Title Name7", outputSeven.getTitle());
		assertNotNull(outputSeven.getLaunchDate());

		Book outputTwelve = outputList.get(12);

		assertEquals(Long.valueOf(12L), outputTwelve.getId());
		assertEquals("Author Name12", outputTwelve.getAuthor());
		assertEquals(65.0, outputTwelve.getPrice());
		assertEquals("Title Name12", outputTwelve.getTitle());
		assertNotNull(outputTwelve.getLaunchDate());
	}
}
