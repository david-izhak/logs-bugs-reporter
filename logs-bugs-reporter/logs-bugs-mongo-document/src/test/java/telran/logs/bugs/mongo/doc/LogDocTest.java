package telran.logs.bugs.mongo.doc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.*;

import javax.validation.ValidationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ContextConfiguration;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;

// @ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = LogsRepo.class)
@EnableAutoConfiguration
// @AutoConfigureDataMongo
@DataMongoTest
public class LogDocTest {
	
	@Autowired
	LogsRepo logs;
	
	@BeforeEach
	void setUp() {
		logs.deleteAll();
	}

	@Test
	void docStoreTest() {
		LogDto logDto = new LogDto(new Date(), LogType.NO_EXCEPTION, "artifact", 20, "result");
		logs.save(new LogDoc(logDto));
		LogDoc actualDoc = logs.findAll().get(0);
		assertEquals(logDto, actualDoc.getLogDto());
	}
	
	@Test
	void logDtoValidationViolationTest() {
		LogDto logDtoDateValidationViolation = new LogDto(null, LogType.NO_EXCEPTION, "artifact", 20, "result");
		LogDto logDtoLogTypeValidationViolation = new LogDto(new Date(), null, "artifact", 20, "result");
		LogDto logDtoArtifactValidationViolationEmpty = new LogDto(new Date(), LogType.NO_EXCEPTION, "", 20, "result");
		LogDto logDtoArtifactValidationViolationNull = new LogDto(new Date(), LogType.NO_EXCEPTION, null, 20, "result");
		assertThrows(ValidationException.class, () -> logs.save(new LogDoc(logDtoDateValidationViolation)));
		assertThrows(ValidationException.class, () -> logs.save(new LogDoc(logDtoLogTypeValidationViolation)));
		assertThrows(ValidationException.class, () -> logs.save(new LogDoc(logDtoArtifactValidationViolationEmpty)));
		assertThrows(ValidationException.class, () -> logs.save(new LogDoc(logDtoArtifactValidationViolationNull)));
	}
	
}
