package telran.logs.bugs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
	
	@Value("${app-binding-name:exceptions-out-0}")
	String bindingName;

	@Autowired
	InputDestination input;

	@Autowired
	LogsRepoPopulator logs;
	
	@BeforeEach
	void setUp() {
		logs.deleteAll().block();
	}

	@Test
	void takeLogDto() {
		LogDto logDto = new LogDto(new Date(), LogType.NO_EXCEPTION, "artifact", 20, "result");
		sendLog(logDto);
		List<LogDoc> actualDocs = logs.findAll().buffer(Duration.ofMillis(100)).blockFirst();
		assertEquals(logDto, actualDocs.get(0).getLogDto());
	}

	private void sendLog(LogDto logDto) {
		input.send(new GenericMessage<LogDto>(logDto));
	}
}
