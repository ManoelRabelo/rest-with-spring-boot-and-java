package br.com.semeru.services;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import br.com.semeru.controllers.BookController;
import br.com.semeru.controllers.PersonController;
import br.com.semeru.data.vo.v1.BookVO;
import br.com.semeru.exceptions.RequiredObjectIsNullException;
import br.com.semeru.exceptions.ResourceNotFoundException;
import br.com.semeru.mapper.DozerMapper;
import br.com.semeru.model.Book;
import br.com.semeru.reposirories.BookRepository;

@Service
public class BookServices {

	private Logger logger = Logger.getLogger(BookServices.class.getName());

	@Autowired
	BookRepository repository;

	@Autowired
	PagedResourcesAssembler<BookVO> assembler;

	public PagedModel<EntityModel<BookVO>> findAll(Pageable pageable) {

		logger.info("Finding all Books!");
		
		var bookPage = repository.findAll(pageable);
		
		var booksVosPage = bookPage.map(b -> DozerMapper.parseObject(b, BookVO.class));
		booksVosPage.map(
			b -> b.add(linkTo(methodOn(BookController.class)
				.findById(b.getKey())).withSelfRel()));

		Link link = linkTo(methodOn(PersonController.class)
			.findAll(
				pageable.getPageNumber(),
				pageable.getPageSize(),
				"asc")).withSelfRel();
		
		return assembler.toModel(booksVosPage, link);
	}

	public BookVO findById(Long id) {

		logger.info("Finding one Book!");

		var entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found fo this ID!"));

		var vo = DozerMapper.parseObject(entity, BookVO.class);

		vo.add(linkTo(methodOn(BookController.class).findById(id)).withSelfRel());

		return vo;
	}

	public BookVO create(BookVO book) {

		if (book == null)
			throw new RequiredObjectIsNullException();

		logger.info("Creating all Books!");

		var entity = DozerMapper.parseObject(book, Book.class);
		var vo = DozerMapper.parseObject(repository.save(entity), BookVO.class);

		vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());

		return vo;
	}

	public BookVO update(BookVO book) {

		if (book == null)
			throw new RequiredObjectIsNullException();

		logger.info("Updating all Books!");

		var entity = repository.findById(book.getKey())
				.orElseThrow(() -> new ResourceNotFoundException("No records found fo this ID!"));

		entity.setAuthor(book.getAuthor());
		entity.setLaunchDate(book.getLaunchDate());
		entity.setPrice(book.getPrice());
		entity.setTitle(book.getTitle());

		var vo = DozerMapper.parseObject(repository.save(entity), BookVO.class);

		vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());

		return vo;
	}

	public void delete(Long id) {

		logger.info("Deleting one book!");

		var entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found fo this ID!"));

		repository.delete(entity);
	}
}
