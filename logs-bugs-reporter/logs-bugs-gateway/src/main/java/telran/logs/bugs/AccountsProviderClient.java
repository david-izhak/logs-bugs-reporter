package telran.logs.bugs;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.log4j.Log4j2;
import telran.logs.bugs.mongo.documents.AccountDoc;

@Component
@Log4j2
public class AccountsProviderClient {
	
	@Autowired
	LoadBalancer loadBalancer;
	
	List <AccountDoc> accounts = new ArrayList <>();
	public List <AccountDoc> getAccountsDoc() {
		return accounts;
	}
	
	@Value("${spring.profiles.active}")
	String springProfilesActive;
	
	RestTemplate restTemplate = new RestTemplate();
	
	String urlAccountsProvider = null;

	public List <AccountDoc> getAccounts() {
		log.debug(">>>> AccountsProviderClient > getAccounts: start method.");
		log.debug(">>>> AccountsProviderClient > getAccounts: check springProfilesActive: {}", springProfilesActive);
		List <AccountDoc> res;
		try {
			log.debug(">>>> AccountsProviderClient > getAccounts: try to receav responseEntity from AccountsProvider");
			ResponseEntity<List <AccountDoc>> responseEntity = restTemplate.exchange(urlAccountsProvider + "/active_accounts", HttpMethod.GET, null, new ParameterizedTypeReference<List<AccountDoc>>(){});
			log.debug(">>>> AccountsProviderClient > getAccounts: receaved responseEntity {}", responseEntity);
			res = responseEntity.getBody();
			log.debug(">>>> AccountsProviderClient > getAccounts: receaved list {}", res);
		} catch (RestClientException e) {
			e.printStackTrace();
			res = null; // TODO ?
			log.debug(">>>> AccountsProviderClient > getAccounts: in bloc catch");
		}
		log.debug("Accounts list is {}", res);
		log.debug(">>>> AccountsProviderClient > getAccounts: res {}", res);
		return res;
	}
	
	private String getUrlAccountsProvider() {
		if(springProfilesActive.equals("dev")) {
			return "http://localhost:8787/active_accounts";
		}
		log.debug(">>>> AccountsProviderClient > getUrlAccountsProvider: start method");
		String urlAccountsProvider = loadBalancer.getBaseUrl("accounts-provider");
		log.debug(">>>> AccountsProviderClient > getUrlAccountsProvider: Recieved URL of a service that provides accounts: {}", urlAccountsProvider);
		return urlAccountsProvider;
	}
	
	@PostConstruct
	void setAccounts() {
		urlAccountsProvider = getUrlAccountsProvider();
		log.debug(">>>> AccountsProviderClient > setAccounts: {}", urlAccountsProvider);
		accounts = getAccounts();
	}
}
