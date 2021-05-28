package telran.logs.bugs.controller;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import telran.logs.bugs.accounting.mongo.documents.AccountDoc;
import telran.logs.bugs.accounting.mongo.repo.AccountRepository;

@RestController
@Log4j2
public class AccountsProviderController {
	
	@Autowired
	AccountRepository accountRepository;
	
	@GetMapping(value="/active_accounts", produces="application/json")
	public Flux<AccountDoc> getAllActiveAccounts() {
		Flux<AccountDoc> result = accountRepository.findByExpirationTimestampGreaterThan(Instant.now().getEpochSecond());
		log.debug(">>> AccountsProviderController >> getAllActiveAccounts: {}", result != null);
		return result;
	}

	// TODO test it
	@GetMapping(value="/get_account/{username}", produces="application/json")
	public Mono<AccountDoc> getAccountByUsername(@PathVariable String username) {
		Mono<AccountDoc> result = accountRepository.findByExpirationTimestampGreaterThanAndUserNameIgnoreCase(Instant.now().getEpochSecond(), username);
		log.debug(">>> AccountsProviderController >> getAccountByUsername: {}", result != null);
		return result;
	}
}
