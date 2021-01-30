package telran.logs.bugs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	
	static Logger LOG = LoggerFactory.getLogger(LogsDbPopulatorAppl.class);

	@BeforeEach
	void setUp() {
		logs.deleteAll();
	}

	@Test
	void takeLogDto() {
		LogDto logDto = new LogDto(new Date(), LogType.NO_EXCEPTION, "artifact", 20, "result");
		sendLog(logDto);
		LogDoc actualDoc = logs.findAll().get(0);
		assertEquals(logDto, actualDoc.getLogDto());
	}

	@Test
	void logDtoValidationViolationDateTest() {
		LogDto logDtoDateValidationViolation = new LogDto(null, LogType.NO_EXCEPTION, "artifact", 20, "result");
		executeTestWithValidationViolation(logDtoDateValidationViolation);
	}

	@Test
	void logDtoValidationViolationLogTypeTest() {
		LogDto logDtoLogTypeValidationViolation = new LogDto(new Date(), null, "artifact", 20, "result");
		executeTestWithValidationViolation(logDtoLogTypeValidationViolation);
	}

	@Test
	void logDtoValidationViolationArtifactEmptyTest() {
		LogDto logDtoArtifactValidationViolationEmpty = new LogDto(new Date(), LogType.NO_EXCEPTION, "", 20, "result");
		executeTestWithValidationViolation(logDtoArtifactValidationViolationEmpty);
	}

	@Test
	void logDtoValidationViolationArtifactNullTest() {
		LogDto logDtoArtifactValidationViolationNull = new LogDto(new Date(), LogType.NO_EXCEPTION, null, 20, "result");
		executeTestWithValidationViolation(logDtoArtifactValidationViolationNull);
	}
	
	void executeTestWithValidationViolation(LogDto logDtoWithValidationViolation) {
		sendLog(logDtoWithValidationViolation);
		assertEquals(1, logs.findAll().size());
		assertEquals(LogType.BAD_REQUEST_EXCEPTION, logs.findAll().get(0).getLogDto().logType);
		assertEquals(LogsDbPopulatorAppl.class.toString(), logs.findAll().get(0).getLogDto().artifact);
		assertEquals(0, logs.findAll().get(0).getLogDto().responseTime);
		assertTrue(logs.findAll().get(0).getLogDto().result.contains("ConstraintViolationImpl"));
	}

	private void sendLog(LogDto logDto) {
		input.send(new GenericMessage<LogDto>(logDto));
	}
}
