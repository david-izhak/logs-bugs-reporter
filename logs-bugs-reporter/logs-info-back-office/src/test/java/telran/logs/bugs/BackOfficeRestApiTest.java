package telran.logs.bugs;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.reactive.server.WebTestClient;

import lombok.extern.slf4j.Slf4j;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Slf4j
public class BackOfficeRestApiTest {
	
	@Autowired
	WebTestClient webTestClient;
	
	@Value("${path-get_all_logs}")
	String	pathGetAllLogs;

	@Value("${path-get_logs_by_type-base}")
	String pathGetLogsByTypeBase;
	
	@Value("${back-office-test-path-get_all_exceptions}")
	String	pathGetAllExceptions;
	
	@Value("${app-number-logs}")
	int numberLogs;
	
	@Test
	void pu() {
		assertEquals(1, 1);
	}
	
	@Test
	void getAllLogsTest() {
		List <LogDto> list = queryListLogDto(pathGetAllLogs);
		assertEquals(numberLogs, list.size());
	}
	
	@Test
	void getLogsByTypeTest() {
		Arrays.stream(LogType.values()).forEach(logType -> {
			List <LogDto> list = queryListLogDto(pathGetLogsByTypeBase + logType);
			list.forEach(logDto -> assertEquals(logDto.logType, logType));
			log.debug("===> Test: asserted query for logType {}. Size of the responds list {}.", logType, list.size());
		});
	}

	@Test
	void getLogsAllExceptionsTest() {
		List <LogDto> list = queryListLogDto(pathGetAllExceptions);
		list.forEach(logDto -> assertNotEquals(logDto, LogType.NO_EXCEPTION));
		log.debug("===> Test: asserted query for all exceptions. Size of the responds list {}.", list.size());
	}
	
	private List <LogDto> queryListLogDto(String path) {
		return webTestClient.get()
		.uri(path)
		.exchange()
		.expectStatus().isOk()
		// v1
		.expectBodyList(LogDto.class)
		.returnResult()
		.getResponseBody();
		// v2
		//.returnResult(LogDto.class)
		//.getResponseBody()
		//.collectList()
		//.block();
	}
}
