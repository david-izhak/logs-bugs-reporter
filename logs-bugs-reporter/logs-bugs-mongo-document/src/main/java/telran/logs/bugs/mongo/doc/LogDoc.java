package telran.logs.bugs.mongo.doc;

import java.util.Date;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

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

	@NonNull private Date dateTime;
	@NonNull private LogType logType;
	@NonNull private String artifact;
	@NonNull private Integer responseTime;
	@NonNull private String result;

	public LogDoc(LogDto logDto) {
		
		validateLogDto(logDto);
		
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
	
	private void validateLogDto(LogDto logDto) {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set <ConstraintViolation<LogDto>> violations = validator.validate(logDto);
		if(!violations.isEmpty()) {
			throw new ValidationException("Ther is some violation of LogDto validations");
		}
	}
}
