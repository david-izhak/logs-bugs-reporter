package telran.logs.bugs.repo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
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

//	private static final String COUNT = "count";
//	@Autowired
//	ReactiveMongoTemplate mongoTemplate;
//	
//	@Override
//	public Flux<LogTypeCount> getLogTypeCounts() {
//		GroupOperation groupOperation = Aggregation.group(LogDoc.LOG_TYPE).count().as(COUNT);
//		ProjectionOperation projectionOperation = Aggregation.project(COUNT).and("_id").as(LogTypeCount.LOG_TYPE);
//		SortOperation sortOperation = Aggregation.sort(Direction.DESC, COUNT);
//		TypedAggregation<LogDoc> pipeLine = Aggregation.newAggregation(LogDoc.class, groupOperation, sortOperation, projectionOperation);
//		return mongoTemplate.aggregate(pipeLine, LogTypeCount.class);
//	}
//
//	@Override
//	public Flux<LogType> getMostEncounteredExceptionTypes(int nExceptions) {
//		MatchOperation matchOperation = Aggregation.match(Criteria.where(LogDoc.LOG_TYPE).ne("NO_EXCEPTION"));
//		GroupOperation groupOperation = Aggregation.group(LogDoc.LOG_TYPE).count().as(COUNT);
//		SortOperation sortOperation = Aggregation.sort(Direction.DESC, COUNT);
//		LimitOperation limitOperation = Aggregation.limit(nExceptions);
//		ProjectionOperation projectionOperation = Aggregation.project(COUNT).and("_id").as(LogTypeCount.LOG_TYPE);
//		TypedAggregation<LogDoc> pipeLine = Aggregation.newAggregation(LogDoc.class, matchOperation, groupOperation, sortOperation, limitOperation, projectionOperation);
//		return mongoTemplate.aggregate(pipeLine, LogTypeCount.class).map(logTypeCount -> logTypeCount.logType);
//	}
//	
//	@Override
//	public Flux<ArtifactCount> getArtifactOccurrences(){
//		GroupOperation groupOperation = Aggregation.group(ArtifactCount.ARTIFACT).count().as(COUNT);
//		SortOperation sortOperation = Aggregation.sort(Direction.DESC, COUNT);
//		ProjectionOperation projectionOperation = Aggregation.project(COUNT).and("_id").as(ArtifactCount.ARTIFACT);
//		TypedAggregation<LogDoc> pipeLine = Aggregation.newAggregation(LogDoc.class, groupOperation, sortOperation, projectionOperation);
//		return mongoTemplate.aggregate(pipeLine, ArtifactCount.class);
//	};
//	
//	@Override
//	public Flux<String> getMostEncounterdArtifacts(int nArtifacts){
//		GroupOperation groupOperation = Aggregation.group(ArtifactCount.ARTIFACT).count().as(COUNT);
//		SortOperation sortOperation = Aggregation.sort(Direction.DESC, COUNT);
//		LimitOperation limitOperation = Aggregation.limit(nArtifacts);
//		ProjectionOperation projectionOperation = Aggregation.project(COUNT).and("_id").as(ArtifactCount.ARTIFACT);
//		TypedAggregation<LogDoc> pipeLine = Aggregation.newAggregation(LogDoc.class, groupOperation, sortOperation, limitOperation, projectionOperation);
//		return mongoTemplate.aggregate(pipeLine, ArtifactCount.class).map(artifactCount -> artifactCount.artifact);
////		return mongoTemplate.aggregate(pipeLine, ArtifactCount.class).map(artifactCount -> String.format("%s%n", artifactCount.artifact));
//	};

	private static final String COUNT = "count";

	@Autowired
	ReactiveMongoTemplate mongoTemplate;

	@Override
	public Flux<LogTypeCount> getLogTypeCounts() {
		String groupField = LogDoc.LOG_TYPE;
		String outputField = LogTypeCount.LOG_TYPE;
		TypedAggregation<LogDoc> pipeline = getGroupPipeline(groupField, outputField, new ArrayList<>());
		return mongoTemplate.aggregate(pipeline, LogTypeCount.class);
	}

	@Override
	public Flux<ArtifactCount> getArtifactOccurrences() {
		TypedAggregation<LogDoc> pipeline = getGroupPipeline(LogDoc.ARTIFACT, ArtifactCount.ARTIFACT, new ArrayList<>());
		return mongoTemplate.aggregate(pipeline, ArtifactCount.class);
	}

	private TypedAggregation<LogDoc> getGroupPipeline(String groupField, String outputField, List<AggregationOperation> aggregationOperations) {
		fillListOperations(groupField, outputField, aggregationOperations);
		TypedAggregation<LogDoc> pipeline = Aggregation.newAggregation(LogDoc.class, aggregationOperations);
		return pipeline;
	}

	private void fillListOperations(String groupField, String outputField, List<AggregationOperation> aggregationOperations) {
		GroupOperation groupOperation = Aggregation.group(groupField).count().as(COUNT);
		ProjectionOperation projOperation = Aggregation.project(COUNT).and("_id").as(outputField);
		SortOperation sortOperation = Aggregation.sort(Direction.DESC, COUNT);
		aggregationOperations.add(groupOperation);
		aggregationOperations.add(sortOperation);
		aggregationOperations.add(projOperation);
	}

	@Override
	public Flux<ArtifactCount> getMostEncounterdArtifacts(int nArtifacts) {
		TypedAggregation<LogDoc> pipeline = getGroupPipeline(LogDoc.ARTIFACT, ArtifactCount.ARTIFACT, new ArrayList<>(), nArtifacts);
		return mongoTemplate.aggregate(pipeline, ArtifactCount.class);
	}

	private TypedAggregation<LogDoc> getGroupPipeline(String groupField, String outputField, ArrayList<AggregationOperation> aggregationOperations, int limit) {
		fillListOperations(groupField, outputField, aggregationOperations);
		aggregationOperations.add(Aggregation.limit(limit));
		TypedAggregation<LogDoc> pipeline = Aggregation.newAggregation(LogDoc.class, aggregationOperations);
		return pipeline;
	}

	@Override
	public Flux<LogTypeCount> getMostEncounteredExceptionTypes(int nExceptions) {
		ArrayList<AggregationOperation> aggregationOperations = new ArrayList<>();
		aggregationOperations.add(Aggregation.match(Criteria.where(LogDoc.LOG_TYPE).ne(LogType.NO_EXCEPTION)));
		TypedAggregation<LogDoc> pipline = getGroupPipeline(LogDoc.LOG_TYPE, LogTypeCount.LOG_TYPE, aggregationOperations, nExceptions);
		return mongoTemplate.aggregate(pipline, LogTypeCount.class);
	}
}
