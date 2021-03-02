package telran.logs.bugs.exceptions;

@SuppressWarnings("serial")
public class DuplicatedKeyException extends RuntimeException {
	public DuplicatedKeyException(String message){
		super(message);
	}

}
