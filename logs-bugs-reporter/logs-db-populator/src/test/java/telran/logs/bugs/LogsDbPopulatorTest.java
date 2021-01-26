package telran.logs.bugs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;

import javax.validation.ValidationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.support.GenericMessage;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.mongo.doc.LogDoc;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
public class LogsDbPopulatorTest {
	
	@Autowired
	InputDestination input;
	
	@Autowired
	LogsRepoPopulator logs;
	
	@BeforeEach
	void setUp() {
		logs.deleteAll();
	}

	@Test
	void takeLogDto() {
		input.send(new GenericMessage<LogDto>(new LogDto(new Date(), LogType.NO_EXCEPTION, "artifact", 20, "result")));
		// TODO testing of saving LogDto into MongoDB
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
