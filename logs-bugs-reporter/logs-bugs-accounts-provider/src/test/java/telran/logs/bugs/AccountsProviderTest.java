package telran.logs.bugs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.reactive.server.WebTestClient;

import lombok.extern.log4j.Log4j2;
import telran.logs.bugs.accounting.mongo.documents.AccountDoc;
import telran.logs.bugs.accounting.mongo.repo.AccountRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Log4j2
public class AccountsProviderTest {
	
	@Autowired
	WebTestClient webTestClient;
	
	@Autowired
	AccountRepository accountRepository;
	
	List<AccountDoc> listAccountDocs = Arrays.asList(
			new AccountDoc ("user1", "password1", new String[] {"user"}, 1600000001, 1700000001),
			new AccountDoc ("user2", "password2", new String[] {"user"}, 1600000002, 1700000002));
	
	@Test
	void emailExisting() {
		accountRepository.saveAll(listAccountDocs).blockLast();
		log.debug(">>>> accountRepository.count: {}", accountRepository.count());
		
		List<AccountDoc> listRes = webTestClient.get()
		.uri("/active_accounts").exchange()
		.expectStatus().isOk()
		.expectBodyList(AccountDoc.class)
		.returnResult()
		.getResponseBody();
		assertTrue(listAccountDocs.containsAll(listRes));
		
		webTestClient.get()
		.uri("/active_accounts").exchange()
		.expectStatus().isOk()
		.expectBodyList(AccountDoc.class)
		.isEqualTo(listAccountDocs);
	}

}
