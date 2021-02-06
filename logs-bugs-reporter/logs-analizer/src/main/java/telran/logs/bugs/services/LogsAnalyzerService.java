package telran.logs.bugs.services;

import java.util.Date;
import java.util.Set;
import java.util.function.Consumer;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;

@Service
@Slf4j
public class LogsAnalyzerService {
	
	@Autowired
	LogsRepo logs;
	
	@Value("${app-binding-name:exceptions-out-0}")
	String bindingName;
	
	@Autowired
	StreamBridge streamBridge;
	
	@Autowired
	Validator validator;
	
	@Bean
	Consumer<LogDto> getAnalyzerBean(){
		log.debug(">>>> Start creating of the consumer log {}", LogDto.class.getName());
		return this::analyzerMethod;
	}
	
	private void analyzerMethod(LogDto logDtoIn){
		log.debug(">>>> recieved log {}", logDtoIn);
		LogDto logDto = analyzeLogDtoViolations(logDtoIn);
		if(logDto.logType != null && logDto.logType != LogType.NO_EXCEPTION) {
			streamBridge.send(bindingName, logDto);
			log.warn(">>>> Recieved log with LogType of exception {}, and sended thru streamBridge", logDto.logType);
		}
	}

	private LogDto analyzeLogDtoViolations(LogDto logDto) {
		log.debug(">>>> Start validation {}", logDto);
		LogDto[] logDtoArray = {logDto};
		Set<ConstraintViolation<LogDto>> violations = validator.validate(logDto);
		if (!violations.isEmpty()) {
			log.debug(">>>> Founded violation");
			violations.forEach(cv -> log.error(">>>> logDto with violation : {}; field: {}; message: {}",logDto,
					cv.getPropertyPath(), cv.getMessage()));
			logDtoArray[0] = new LogDto(new Date(),
					LogType.BAD_REQUEST_EXCEPTION, LogsAnalyzerService.class.getName(), 0, violations.toString());
			log.debug(">>>> Generated new LogDto {}", logDtoArray[0]);
		}
		return logDtoArray[0];
	}
}
