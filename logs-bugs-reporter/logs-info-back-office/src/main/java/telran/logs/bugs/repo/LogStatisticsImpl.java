package telran.logs.bugs.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;

import reactor.core.publisher.Flux;
import telran.logs.bugs.dto.ArtifactCount;
import telran.logs.bugs.dto.LogType;
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
		SortOperation sortOperation = Aggregation.sort(Direction.DESC, COUNT);
		TypedAggregation<LogDoc> pipeLine = Aggregation.newAggregation(LogDoc.class, groupOperation, sortOperation, projectionOperation);
		return mongoTemplate.aggregate(pipeLine, LogTypeCount.class);
	}

	@Override
	public Flux<LogType> getMostEncounteredExceptionTypes(int nExceptions) {
		MatchOperation matchOperation = Aggregation.match(Criteria.where(LogDoc.LOG_TYPE).ne("NO_EXCEPTION"));
		GroupOperation groupOperation = Aggregation.group(LogDoc.LOG_TYPE).count().as(COUNT);
		SortOperation sortOperation = Aggregation.sort(Direction.DESC, COUNT);
		LimitOperation limitOperation = Aggregation.limit(nExceptions);
		ProjectionOperation projectionOperation = Aggregation.project(COUNT).and("_id").as(LogTypeCount.LOG_TYPE);
		TypedAggregation<LogDoc> pipeLine = Aggregation.newAggregation(LogDoc.class, matchOperation, groupOperation, sortOperation, limitOperation, projectionOperation);
		return mongoTemplate.aggregate(pipeLine, LogTypeCount.class).map(logTypeCount -> logTypeCount.logType);
	}
	
	@Override
	public Flux<ArtifactCount> getArtifactOccurrences(){
		GroupOperation groupOperation = Aggregation.group(ArtifactCount.ARTIFACT).count().as(COUNT);
		SortOperation sortOperation = Aggregation.sort(Direction.DESC, COUNT);
		ProjectionOperation projectionOperation = Aggregation.project(COUNT).and("_id").as(ArtifactCount.ARTIFACT);
		TypedAggregation<LogDoc> pipeLine = Aggregation.newAggregation(LogDoc.class, groupOperation, sortOperation, projectionOperation);
		return mongoTemplate.aggregate(pipeLine, ArtifactCount.class);
	};
	
	@Override
	public Flux<String> getMostEncounterdArtifacts(int nArtifacts){
		GroupOperation groupOperation = Aggregation.group(ArtifactCount.ARTIFACT).count().as(COUNT);
		SortOperation sortOperation = Aggregation.sort(Direction.DESC, COUNT);
		LimitOperation limitOperation = Aggregation.limit(nArtifacts);
		ProjectionOperation projectionOperation = Aggregation.project(COUNT).and("_id").as(ArtifactCount.ARTIFACT);
		TypedAggregation<LogDoc> pipeLine = Aggregation.newAggregation(LogDoc.class, groupOperation, sortOperation, limitOperation, projectionOperation);
		return mongoTemplate.aggregate(pipeLine, ArtifactCount.class).map(artifactCount -> artifactCount.artifact);
//		return mongoTemplate.aggregate(pipeLine, ArtifactCount.class).map(artifactCount -> String.format("%s%n", artifactCount.artifact));
	};
}
