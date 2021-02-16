package telran.logs.bugs;

import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;

@SpringBootTest (classes = LogsInfoAppl.class)
@AutoConfigureWebTestClient
public class BackOfficeRestApiTest {
	
	@Autowired
	WebTestClient webTestClient;
	
	@Value("${path-get_all_logs}")
	String	pathGetAllLogs;
	
	@Value("${path-get_logs_by_type-no_exception}")
	String pathGetLogsByTypeNoException;

	@Value("${path-get_logs_by_type-bad_request_exception}")
	String pathGetLogsByTypeBadRequestException;
	
	@Value("${path-get_logs_by_type-not_found_exception}")
	String pathGetLogsByTypeNotFoundException;
	
	@Value("${path-get_logs_by_type-duplicated_key_exception}")
	String pathGetLogsByTypeDuplicatedKeyException;
	
	@Value("${path-get_logs_by_type-server_exception}")
	String pathGetLogsByTypeServerException;
	
	@Value("${path-get_logs_by_type-authentication_exception}")
	String pathGetLogsByTypeAuthenticationException;
	
//	
	@Value("${path-get_logs_by_type-authorization_exception}")
	String pathGetLogsByTypeAuthorizationException;

	@Value("${back-office-test-path-get_all_exceptions}")
	String	pathGetAllExceptions;
	
	@Test
	void getAllLogsTest() {
		List <LogDto> list = queryListLogDto(pathGetAllLogs);
		assertEquals(100000, list.size());
	}
	
	@Test
	void getLogsByTypeTestNoException() {
		List <LogDto> list = queryListLogDto(pathGetLogsByTypeNoException);
		list.forEach(log -> testType(log, LogType.NO_EXCEPTION));
	}
	
	@Test
	void getLogsByTypeTestBadRequestException() {
		List <LogDto> list = queryListLogDto(pathGetLogsByTypeBadRequestException);
		list.forEach(log -> testType(log, LogType.BAD_REQUEST_EXCEPTION));
	}
	
	@Test
	void getLogsByTypeTestNotFoundException() {
		List <LogDto> list = queryListLogDto(pathGetLogsByTypeNotFoundException);
		list.forEach(log -> testType(log, LogType.NOT_FOUND_EXCEPTION));
	}
	
	@Test
	void getLogsByTypeTestDuplicatedKeyException() {
		List <LogDto> list = queryListLogDto(pathGetLogsByTypeDuplicatedKeyException);
		list.forEach(log -> testType(log, LogType.DUPLICATED_KEY_EXCEPTION));
	}
	
	@Test
	void getLogsByTypeTestServerException() {
		List <LogDto> list = queryListLogDto(pathGetLogsByTypeServerException);
		list.forEach(log -> testType(log, LogType.SERVER_EXCEPTION));
	}
	
	@Test
	void getLogsByTypeTestAuthenticationException() {
		List <LogDto> list = queryListLogDto(pathGetLogsByTypeAuthenticationException);
		list.forEach(log -> testType(log, LogType.AUTHENTICATION_EXCEPTION));
	}
	
	@Test
	void getLogsByTypeTestAuthorizationException() {
		List <LogDto> list = queryListLogDto(pathGetLogsByTypeAuthorizationException);
		list.forEach(log -> testType(log, LogType.AUTHORIZATION_EXCEPTION));
	}

	@Test
	void getLogsAllExceptionsTest() {
		List <LogDto> list = queryListLogDto(pathGetAllExceptions);
		list.forEach(log -> assertNotEquals(log, LogType.NO_EXCEPTION));
	}

	private void testType(LogDto log, LogType logType) {
		assertEquals(log.logType, logType);
	}
	
	private List <LogDto> queryListLogDto(String path) {
		return webTestClient.get()
		.uri(path)
		.exchange()
		.expectStatus().isOk()
		.returnResult(LogDto.class)
		.getResponseBody()
		.collectList()
		.block();
	}
}
