package telran.logs.bugs.api;

import org.springframework.web.bind.annotation.PathVariable;

public interface EmailProviderApi {
//	String variables.
	String PATHVARIABLE_NAME  = "artifact";
	String ANSWER_IF_ARTIFACT_NO_EXISTS = "No such artifact and email";
	
//	Paths for REST API.
	String EMAIL_ARTIFACT = "/email/{artifact}";
}
