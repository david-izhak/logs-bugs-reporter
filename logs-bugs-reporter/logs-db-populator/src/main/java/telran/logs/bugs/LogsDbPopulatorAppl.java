package telran.logs.bugs;

import java.util.Date;
import java.util.Set;
import java.util.function.Consumer;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;

import lombok.extern.slf4j.Slf4j;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.mongo.doc.LogDoc;

@SpringBootApplication
@Slf4j
public class LogsDbPopulatorAppl {

	@Autowired
	LogsRepoPopulator logs;

	@Autowired
	Validator validator;
	
	@Value("${app-binding-name:exceptions-out-0}")
	String bindingName;
	@Autowired
	StreamBridge streamBridge;

	public static void main(String[] args) {
		SpringApplication.run(LogsDbPopulatorAppl.class, args);
		log.info("Started {}", LogsDbPopulatorAppl.class.getName());
	}

	@Bean
	Consumer<LogDto> getLogDtoConsumer() {
		return this::takeAndSaveLogDto;
	}

	void takeAndSaveLogDto(LogDto logDto) {
		Set<ConstraintViolation<LogDto>> violations = validator.validate(logDto);
		if (!violations.isEmpty()) {
			violations.forEach(cv -> {
				log.error("Recived LogDto that has violations of the constraint {}", violations.toString());
				LogDto logDtoError = new LogDto(new Date(), LogType.BAD_REQUEST_EXCEPTION,
						LogsDbPopulatorAppl.class.getName(), 0, violations.toString());
				streamBridge.send(bindingName, logDtoError);
				logs.save(new LogDoc(logDtoError));
				log.debug("log about exception after validation violation saved to repositoriy {}", logDtoError);
			});
		} else {
			logs.save(new LogDoc(logDto));
			log.debug("correct log saved to repositoriy {}", logDto);
		}
	}
}

