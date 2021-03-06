package telran.logs.bugs.controllers;

import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import lombok.extern.slf4j.Slf4j;
import telran.logs.bugs.exceptions.DuplicatedKeyException;
import telran.logs.bugs.exceptions.NotFoundException;
import telran.logs.bugs.exceptions.ServerException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionsController {
	
	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	String constraintViolationExceptionHandler(ConstraintViolationException e) {
		return processingException(e);
	}
	
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	String methodArgumentTypeMismatchExceptionHandler(MethodArgumentTypeMismatchException e) {
		return processingException(e);
	}
	
//	@ExceptionHandler(ConversionFailedException.class)
//	@ResponseStatus(HttpStatus.BAD_REQUEST)
//	String conversionFailedExceptionHandler(ConversionFailedException e) {
//		return processingException(e);
//	}

	@ExceptionHandler(DuplicatedKeyException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	String duplicatedKeyExceptionHandler (DuplicatedKeyException duplicatedKeyException) {
		return processingException(duplicatedKeyException);
	}
	
	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	String notFoundExceptionHandler (NotFoundException notFoundException) {
		return processingException(notFoundException);
	}
	
	@ExceptionHandler(ServerException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	String serverExceptionHandler (ServerException serverException) {
		return processingException(serverException);
	}
	
	@ExceptionHandler(RuntimeException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	String runtimeExceptionHandler (RuntimeException runtimeException) {
		return processingException(runtimeException);
	}

	private String processingException(Throwable e) {
		log.error("===> Exception class: {}, message: {}.", e.getClass().getSimpleName(), e.getMessage());
		return e.getMessage();
	}
}
