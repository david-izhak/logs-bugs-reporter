package telran.logs.bugs.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EmailProviderClient {
	
	RestTemplate restTemplate = new RestTemplate();
	@Value("${app-url-assigner-mail:xxx}")
	String urlAssignerMail;
	
	@Value("${app-url-programmer-mail:xxx}")
	String urlProgrammerMail;

	public String getEmailByArtifact(String artifact) {
		String res;
		try {
			ResponseEntity<String> responseEntity = restTemplate.exchange(getUrlProgrammist(artifact), HttpMethod.GET, null, String.class);
			res = responseEntity.getBody();
		} catch (RestClientException e) {
			e.printStackTrace();
			res = "";
		}
		log.debug("Programmist email is {}", res);
		return res;
	}
	
	private String getUrlProgrammist(String artifact) {
		String res = urlProgrammerMail + "/email/" + artifact; //TODO move property to interface
		log.debug("URL for getting programmist email is {}", res);
		return res;
	}

	public String getAssignerMail () {
		String res;
		try {
			ResponseEntity<String> responseEntity = restTemplate.exchange(getUrlAssigner(), HttpMethod.GET, null, String.class);
			res = responseEntity.getBody();
		} catch (RestClientException e) {
			e.printStackTrace();
			res = "";
		}
		log.debug("Assigner email is {}", res);
		return res;
	}

	private String getUrlAssigner() {
		String res = urlAssignerMail + "/email/assigner"; //TODO move property to interface
		log.debug("URL for getting assigner email is {}", res);
		return res;
	}
}
