package telran.logs.bugs;

import java.util.Date;
import java.util.Set;
import java.util.function.Consumer;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.mongo.doc.LogDoc;

@SpringBootApplication
public class LogsDbPopulatorAppl {

	@Autowired
	LogsRepoPopulator logs;

	@Autowired
	Validator validator;
	
	@Value("${app-binding-name:exceptions-out-0}") // Что за нейминг? Где он описан? Что он означает?
	String bindingName;
	@Autowired
	StreamBridge streamBridge; // Что за класс? Где он описан? В чём его функциональность?

	static Logger LOG = LoggerFactory.getLogger(LogsDbPopulatorAppl.class);

	public static void main(String[] args) {
		SpringApplication.run(LogsDbPopulatorAppl.class, args);
	}

	@Bean
	Consumer<LogDto> getLogDtoConsumer() {
		return this::takeAndSaveLogDto;
	}

	void takeAndSaveLogDto(LogDto logDto) {
		Set<ConstraintViolation<LogDto>> violations = validator.validate(logDto);
		if (!violations.isEmpty()) {
			violations.forEach(cv -> {
				LOG.error("* ERROR * Recived LogDto that has violations of the constraint {}", violations.toString());
				LogDto logDtoError = new LogDto(new Date(), LogType.BAD_REQUEST_EXCEPTION,
						LogsDbPopulatorAppl.class.toString(), 0, cv.toString());
				streamBridge.send(bindingName, logDtoError); // Куда отправляется сообщение? И зачем?
				logs.save(new LogDoc(logDtoError));
				LOG.debug("# DEBUG 2 # log about exception after validation violation saved to repositoriy {}", logDtoError);
			});
		} else {
			logs.save(new LogDoc(logDto));
			LOG.debug("# DEBUG 1 # correct log saved to repositoriy {}", logDto);
		}
	}
}
