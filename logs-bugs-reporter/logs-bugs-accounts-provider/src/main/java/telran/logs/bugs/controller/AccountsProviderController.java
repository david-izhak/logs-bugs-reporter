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
	AccountRepository aAccountRepository;
	
	@GetMapping(value="/active_accounts", produces="application/stream+json")
	Flux<AccountDoc> getAllLogs() {
		Flux<AccountDoc> result = aAccountRepository.findByExpirationTimestampGreaterThan(Instant.now().getEpochSecond());
		log.debug("Logs sent to a client");
		return result;
	}

}
