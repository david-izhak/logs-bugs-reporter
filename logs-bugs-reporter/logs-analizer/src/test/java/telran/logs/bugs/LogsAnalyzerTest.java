package telran.logs.bugs;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
public class LogsAnalyzerTest {
	
	static Logger LOG = LoggerFactory.getLogger(LogsAnalyzerTest.class);
	
	@Autowired
	InputDestination producer;
	
	@Autowired
	OutputDestination consumer;
	
	@Value("${app-binding-name}")
	String bindinName;
	
	@BeforeEach
	void setup() {
		consumer.clear();
	}
	
	@Test
	void analyzerTestNonException() {
		LogDto logDto = new LogDto(new Date(), LogType.NO_EXCEPTION, "artifact", 20, "result");
		producer.send(new GenericMessage<LogDto>(logDto));
		assertThrows(Exception.class, consumer::receive);
	}
	
	@Test
	void analyzerTestException() {
		LogDto logDtoException = new LogDto(new Date(), LogType.AUTHENTICATION_EXCEPTION, "artifact", 20, "result");
		producer.send(new GenericMessage<LogDto>(logDtoException));
		Message<byte[]> messag = consumer.receive(0, bindinName);
		assertNotNull(messag);
		LOG.debug("recieved in consumer {}", new String(messag.getPayload()));
	}
}
