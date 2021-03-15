package telran.logs.bugs.api;

public interface EmailProviderApi {
//	String variables.
	String PATHVARIABLE_NAME  = "artifact";
	String ANSWER_IF_APPROPRIATE_NO_EXISTS = "No such artifact and email";
	
//	Paths for REST API.
	String EMAIL_ARTIFACT = "/email/{artifact}";
}
