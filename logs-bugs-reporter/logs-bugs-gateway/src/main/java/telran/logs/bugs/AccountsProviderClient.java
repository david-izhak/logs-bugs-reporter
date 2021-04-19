package telran.logs.bugs;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.log4j.Log4j2;
import telran.logs.bugs.accounting.mongo.documents.AccountDoc;

@Component
@Log4j2
public class AccountsProviderClient {
	
	@Value("${test.mod}")
	boolean testMod;

	private static final String ACCOUNTS_PROVIDER = "accounts-provider";
	private static final String LOCALHOST = "http://localhost:8787";

	@Autowired
	LoadBalancer loadBalancer;

	List<AccountDoc> accounts = new ArrayList<>();

	public List<AccountDoc> getAccountsDoc() {
		return accounts;
	}

	@Value("${spring.profiles.active}")
	String springProfilesActive;

	RestTemplate restTemplate = new RestTemplate();

	String urlAccountsProvider = null;

	public List<AccountDoc> getAccounts() {
		log.debug(">>>> AccountsProviderClient > getAccounts: start method.");
		log.debug(">>>> AccountsProviderClient > getAccounts: check springProfilesActive: {}", springProfilesActive);
		List<AccountDoc> res = null;
//		restTemplate.getInterceptors().add(
//				  new BasicAuthenticationInterceptor("user1", "11111111"));
		try {
			log.debug(">>>> AccountsProviderClient > getAccounts: try to receav responseEntity from AccountsProvider");
			ResponseEntity<List<AccountDoc>> responseEntity = null;
			try {
				responseEntity = restTemplate.exchange(urlAccountsProvider + "/active_accounts", HttpMethod.GET, null,
						new ParameterizedTypeReference<List<AccountDoc>>(){});
				res = responseEntity.getBody();
			} catch (Exception e) {
				log.error("Error: {}", e.getMessage());
			}
			for (int i = 0; i < 5 && responseEntity == null && !testMod; i++) {
				log.debug(">>> AccountsProviderClient >> getAccounts: There is no an answer from accounts-provider, responseEntity null.");
				try {
					log.debug(">>> AccountsProviderClient >> getAccounts: Waiting 10 sec.");
					Thread.sleep(5000);
					log.debug(
							">>> AccountsProviderClient >> getAccounts: Next attempt to receive an answer from accounts-provider.");
				} catch (InterruptedException e) {
					log.error("Error: {}", e.getMessage());
				}
				try {
					responseEntity = restTemplate.exchange(urlAccountsProvider + "/active_accounts", HttpMethod.GET, null,
							new ParameterizedTypeReference<List<AccountDoc>>(){});
					log.debug(">>>> AccountsProviderClient > getAccounts: receaved responseEntity {}", responseEntity);
					res = responseEntity.getBody();
					log.debug(">>>> AccountsProviderClient > getAccounts: receaved list {}", res);
				} catch (Exception e) {
					log.error("Error: {}", e.getMessage());
				}
			}
		} catch (RestClientException e) {
			log.error("Error: {}", e.getMessage());
			res = null;
			log.debug(">>>> AccountsProviderClient > getAccounts: in bloc catch");
		}
		log.debug("Accounts list is {}", res);
		log.debug(">>>> AccountsProviderClient > getAccounts: res {}", res);
		return res;
	}

	private String getUrlAccountsProvider() {
		if (springProfilesActive.equals("dev")) {
			return LOCALHOST;
		}
		log.debug(">>>> AccountsProviderClient > getUrlAccountsProvider: start method");
		String baseUrl = loadBalancer.getBaseUrl(ACCOUNTS_PROVIDER);
		log.debug(
				">>>> AccountsProviderClient > getUrlAccountsProvider: Recieved URL of a service that provides accounts: {}",
				baseUrl);
		return baseUrl;
	}

	@PostConstruct
	void setAccounts() {
		urlAccountsProvider = getUrlAccountsProvider();
		log.debug(">>>> AccountsProviderClient > setAccounts: {}", urlAccountsProvider);
		accounts = getAccounts();
	}
}
