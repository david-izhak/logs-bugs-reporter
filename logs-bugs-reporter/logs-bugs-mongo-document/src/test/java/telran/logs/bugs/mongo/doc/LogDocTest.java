package telran.logs.bugs.mongo.doc;

import java.util.*;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ContextConfiguration;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;

@ContextConfiguration(classes = LogsRepo.class)
@EnableAutoConfiguration
@DataMongoTest
public class LogDocTest {

	@Autowired
	LogsRepo logs;
	
	@BeforeEach
	void clean() {
		logs.deleteAll().block();
	}

	@Test
	void singleDocStoreTest() {
		LogDto logDto = new LogDto(new Date(), LogType.NO_EXCEPTION, "artifact", 20, "result");
		logs.save(new LogDoc(logDto)).block();
		LogDoc actualDoc = logs.findAll().blockFirst();
		assertEquals(logDto, actualDoc.getLogDto());
	}

	@Test
	void manyDocStoreTest() {
		List<LogType> logTypes = Arrays.stream(LogType.values()).collect(Collectors.toList());
		List<LogDoc> list = new ArrayList<>();
		for (int i = 0; i < 7; i++) {
			list.add(new LogDoc(new LogDto(new Date(), logTypes.get(i), "artifact" + i, i, "result" + i)));
		}
		logs.saveAll(list).blockLast();
		assertEquals(list.size(), logs.count().block());
		
		List<LogDoc> logsList = logs.findAll().collectList().block();
		for (int i = 0; i < 7; i++) {
			assertEquals(list.get(i).getLogDto(), logsList.get(i).getLogDto());
		}
	}
}
