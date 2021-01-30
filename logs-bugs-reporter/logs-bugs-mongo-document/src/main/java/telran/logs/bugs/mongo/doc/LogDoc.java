package telran.logs.bugs.mongo.doc;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import telran.logs.bugs.dto.*;

@NoArgsConstructor
@RequiredArgsConstructor
@Document(collection = "logs")
public class LogDoc {

	@Id
	ObjectId id;

	@NonNull
	private Date dateTime;
	@NonNull
	private LogType logType;
	@NonNull
	private String artifact;
	@NonNull
	private Integer responseTime;
	@NonNull
	private String result;

	public LogDoc(LogDto logDto) {
		dateTime = logDto.dateTime;
		logType = logDto.logType;
		artifact = logDto.artifact;
		responseTime = logDto.responseTime;
		result = logDto.result;

	}

	public ObjectId getId() {
		return id;
	}

	public LogDto getLogDto() {
		return new LogDto(dateTime, logType, artifact, responseTime, result);
	}
}
