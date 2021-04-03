package telran.logs.bugs.client;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import telran.logs.bugs.LoadBalancer;

@Component
@Slf4j
public class EmailProviderClient {
	
	@Autowired
	LoadBalancer loadBalancer;
	
	RestTemplate restTemplate = new RestTemplate();

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
		String urlProgrammerMail = loadBalancer.getBaseUrl("email-provider");
		log.debug(">>>> EmailProviderClient > getUrlProgrammist > Recieved URL of a service that provides progammers' emails: {}", urlProgrammerMail);
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
		String urlAssignerMail = loadBalancer.getBaseUrl("assigner-mail-provider");
		log.debug("Recieved URL of a service that provides assigner email: {}", urlAssignerMail);
		String res = urlAssignerMail + "/email/assigner"; //TODO move property to interface
		log.debug("URL for getting assigner email is {}", res);
		return res;
	}
}
