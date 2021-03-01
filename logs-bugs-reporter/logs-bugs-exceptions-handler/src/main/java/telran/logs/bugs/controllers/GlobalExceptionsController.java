package telran.logs.bugs.controllers;

import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionsController {
	
	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	String constraintViolationExceptionHandler(ConstraintViolationException e) {
		return processingException(e);
	}

	private String processingException(Exception e) {
		log.error("Exception class: {}, message: {}.", e.getClass().getSimpleName(), e.getMessage());
		return null;
	}
}
