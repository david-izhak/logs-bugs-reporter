package telran.logs.bugs;

import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import lombok.extern.slf4j.Slf4j;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.mongo.doc.LogDoc;

@SpringBootApplication
@Slf4j
public class LogsDbPopulatorAppl {

	@Autowired
	LogsRepoPopulator logsRepo;

	public static void main(String[] args) {
		SpringApplication.run(LogsDbPopulatorAppl.class, args);
		log.info("Started {}", LogsDbPopulatorAppl.class.getName());
	}

	@Bean
	Consumer<LogDto> getLogDtoConsumer() {
		return this::takeAndSaveLogDto;
	}

	void takeAndSaveLogDto(LogDto logDto) {
		log.debug("received log", logDto);
		logsRepo.save(new LogDoc(logDto)).log("takeAndSaveLogDto ===>").subscribe();
		log.debug("start saving log to repositoriy {} (Mongo collection)", logDto);
	}
}
