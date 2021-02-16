package telran.logs.bugs;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import telran.logs.bugs.mongo.doc.LogDoc;

public interface LogsRepoPopulator extends ReactiveMongoRepository<LogDoc, ObjectId> {
}
