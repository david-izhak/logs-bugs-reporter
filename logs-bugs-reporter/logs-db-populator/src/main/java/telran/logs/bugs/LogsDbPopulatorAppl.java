package telran.logs.bugs;

import java.util.function.Consumer;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;

import lombok.extern.slf4j.Slf4j;
import telran.logs.bugs.dto.LogDto;
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
		logs.save(new LogDoc(logDto));
		log.debug("log saved to repositoriy {}", logDto);
	}
}
