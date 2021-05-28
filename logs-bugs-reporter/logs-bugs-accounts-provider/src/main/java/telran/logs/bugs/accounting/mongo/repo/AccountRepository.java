package telran.logs.bugs.accounting.mongo.repo;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import telran.logs.bugs.accounting.mongo.documents.AccountDoc;

public interface AccountRepository extends ReactiveMongoRepository<AccountDoc, String> {
	Flux<AccountDoc> findByExpirationTimestampGreaterThan(long timeStampNow);
	Mono<AccountDoc> findByExpirationTimestampGreaterThanAndUserNameIgnoreCase(long timeStampNow, String userName); // TODO test it
}
