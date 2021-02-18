package telran.logs.bugs.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;

import reactor.core.publisher.Flux;
import telran.logs.bugs.dto.LogTypeCount;
import telran.logs.bugs.mongo.doc.LogDoc;

public class LogStatisticsImpl implements LogStatistics {
	
	private static final String COUNT = "count";
	@Autowired
	ReactiveMongoTemplate mongoTemplate;
	
	@Override
	public Flux<LogTypeCount> getLogTypeCounts() {
		GroupOperation groupOperation = Aggregation.group(LogDoc.LOG_TYPE).count().as(COUNT);
		ProjectionOperation projectionOperation = Aggregation.project(COUNT).and("_id").as(LogTypeCount.LOG_TYPE);
		TypedAggregation<LogDoc> pipeLine = Aggregation.newAggregation(LogDoc.class, groupOperation, projectionOperation);
		
		return mongoTemplate.aggregate(pipeLine, LogTypeCount.class);
	}

}
