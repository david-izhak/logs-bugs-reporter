package telran.logs.bugs;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

import lombok.extern.slf4j.Slf4j;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
@Slf4j
public class LogsAnalyzerTest {
		
	@Autowired
	InputDestination producer;
	
	@Autowired
	OutputDestination consumer;
	
	@Value("${app-binding-name-exceptions:exceptions-out-0}")
	String bindingNameExceptions;
	@Value("${app-binding-name-logs:logs-out-0}")
	String bindingNameLogs;
	@Value("${app-logs-provider-artifact:logs-provider}")
	String logsProviderArtifact;
	
	
	@BeforeEach
	void setup() {
		consumer.clear(bindingNameExceptions);
		log.debug("test::: Consumer was cleared before test (BeforeEach)");
	}
//	void clear(OutputDestination outDest) {
//		try {
//			Field f = outDest.getClass().getDeclaredField("messageQueues");
//			f.setAccessible(true);
//			@SuppressWarnings("unchecked")
//			var messageQueues = (Map<String, BlockingQueue<Message<byte[]>>>) f.get(outDest);
//			messageQueues.values().forEach(BlockingQueue::clear);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	@Test
	void analyzerTestNonException() {
		LogDto logDto = new LogDto(new Date(), LogType.NO_EXCEPTION, "artifact", 0, "result");
		log.debug("test::: Created logDto (non exception) {}", logDto.toString());
		sendLog(logDto); 
		log.debug("test::: producer sends logDto");
		assertNotNull(consumer.receive(0, bindingNameLogs));
		assertNull(consumer.receive(0, bindingNameExceptions));
		
	}
	
	@Test
	void analyzerTestException() {
		LogDto logDtoException = new LogDto(new Date(), LogType.AUTHENTICATION_EXCEPTION, "artifact", 20, "result");
		log.debug("test::: Created logDto (with exception) {}", logDtoException.toString());
		sendLog(logDtoException);
		log.debug("test::: producer sends logDtoException");
		Message<byte[]> messag = consumer.receive(0, bindingNameExceptions);
		assertNotNull(messag);
		log.debug("test::: recieved in consumer {}", new String(messag.getPayload()));
	}

	@Test
	void logDtoValidationViolationDateTest() {
		LogDto logDtoDateValidationViolation = new LogDto(null, LogType.NO_EXCEPTION, "artifact", 20, "result");
		log.debug("test::: Created logDto (with violation) {}", "Date");
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
		log.debug("test::: producer sends logDto with exception");
		Message<byte[]> messag = consumer.receive(0, bindingNameExceptions);
		assertNotNull(messag);
		log.debug("test::: recieved in consumer {}", new String(messag.getPayload()));
	}

	private void sendLog(LogDto logDto) {
		producer.send(new GenericMessage<LogDto>(logDto));
	}
}
