package telran.logs.bugs.controller;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import telran.logs.bugs.accounting.mongo.documents.AccountDoc;
import telran.logs.bugs.accounting.mongo.repo.AccountRepository;

@RestController
@Log4j2
public class AccountsProviderController {
	
	@Autowired
	AccountRepository accountRepository;
	
	@GetMapping(value="/active_accounts", produces="application/json")
	Flux<AccountDoc> getAllLogs() {
		Flux<AccountDoc> result = accountRepository.findByExpirationTimestampGreaterThan(Instant.now().getEpochSecond());
		log.debug(">>> AccountsProviderController >> getAllLogs: Logs sent to a client: {}", result);
		return result;
	}
}
