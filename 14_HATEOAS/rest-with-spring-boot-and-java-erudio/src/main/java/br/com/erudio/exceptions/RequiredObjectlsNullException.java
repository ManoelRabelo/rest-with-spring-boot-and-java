package br.com.erudio.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RequiredObjectlsNullException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public RequiredObjectlsNullException() {
		super("It is not allowed to persist a null object!");
	}

	public RequiredObjectlsNullException(String ex) {
		super(ex);
	}
}
