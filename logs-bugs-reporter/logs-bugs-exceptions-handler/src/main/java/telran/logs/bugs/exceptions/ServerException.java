package telran.logs.bugs.exceptions;

@SuppressWarnings("serial")
public class ServerException extends RuntimeException {
	ServerException(String message){
		super(message);
	}
}
