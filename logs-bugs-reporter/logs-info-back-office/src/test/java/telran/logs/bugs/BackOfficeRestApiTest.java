package telran.logs.bugs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.reactive.server.WebTestClient;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import telran.logs.bugs.dto.ArtifactCount;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
import static telran.logs.bugs.dto.LogType.*;
import telran.logs.bugs.dto.LogTypeCount;
import telran.logs.bugs.mongo.doc.LogDoc;
import telran.logs.bugs.repo.LogRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Slf4j
public class BackOfficeRestApiTest {

	@Autowired
	WebTestClient webTestClient;

	@Autowired
	LogRepository logRepository;

	@Value("${path-get_all_logs}")
	String pathGetAllLogs;

	@Value("${path-get_logs_by_type-base}")
	String pathGetLogsByTypeBase;

	@Value("${back-office-test-path-get_all_exceptions}")
	String pathGetAllExceptions;

	@Value("${app-number-logs}")
	int numberLogs;

	static List<LogDto> exceptions;
	static List<LogDto> noExceptions;
	static List<LogDto> allLogs;
	static Date DATE_TIME = new Date();
	static final String ARTIFACT = "artifact";
	private static final String AUTHENTICATION_ERROR = "Authentication error";
	private static final String AUTHORIZATION_ERROR = "Authorization error";

	@BeforeAll
	static void setUpAll() {
		exceptions = new ArrayList<>(Arrays.asList(
				new LogDto(DATE_TIME, AUTHENTICATION_EXCEPTION, "class5", 0, AUTHENTICATION_ERROR),
				new LogDto(DATE_TIME, AUTHENTICATION_EXCEPTION, "class1", 0, AUTHENTICATION_ERROR),
				new LogDto(DATE_TIME, AUTHENTICATION_EXCEPTION, "class2", 0, AUTHENTICATION_ERROR),
				new LogDto(DATE_TIME, AUTHENTICATION_EXCEPTION, "class2", 0, AUTHENTICATION_ERROR),
				new LogDto(DATE_TIME, AUTHENTICATION_EXCEPTION, "class3", 0, AUTHENTICATION_ERROR),
				new LogDto(DATE_TIME, AUTHORIZATION_EXCEPTION, "class3", 0, AUTHORIZATION_ERROR),
				new LogDto(DATE_TIME, AUTHORIZATION_EXCEPTION, "class3", 0, AUTHORIZATION_ERROR),
				new LogDto(DATE_TIME, AUTHORIZATION_EXCEPTION, "class4", 0, AUTHORIZATION_ERROR),
				new LogDto(DATE_TIME, AUTHORIZATION_EXCEPTION, "class4", 0, AUTHORIZATION_ERROR),
				new LogDto(DATE_TIME, BAD_REQUEST_EXCEPTION, "class4", 0, ""),
				new LogDto(DATE_TIME, BAD_REQUEST_EXCEPTION, "class4", 0, ""),
				new LogDto(DATE_TIME, BAD_REQUEST_EXCEPTION, "class5", 0, ""),
				new LogDto(DATE_TIME, DUPLICATED_KEY_EXCEPTION, "class5", 0, ""),
				new LogDto(DATE_TIME, NOT_FOUND_EXCEPTION, "class5", 0, ""),
				new LogDto(DATE_TIME, SERVER_EXCEPTION, "class5", 0, "")));
		noExceptions = new ArrayList<>(Arrays.asList(
				new LogDto(DATE_TIME, NO_EXCEPTION, ARTIFACT, 20, "result"),
				new LogDto(DATE_TIME, NO_EXCEPTION, ARTIFACT, 25, "result"),
				new LogDto(DATE_TIME, NO_EXCEPTION, ARTIFACT, 30, "result")));
		allLogs = new ArrayList<>(noExceptions);
		allLogs.addAll(exceptions);
	}
	
	@BeforeEach
	void cleanAndFill() {
		logRepository.deleteAll().block();
		setUpDbInitial();
	}
	
	private void setUpDbInitial() {
		Flux<LogDoc> savingFlux = logRepository.saveAll(allLogs.stream().map(LogDoc::new).collect(Collectors.toList()));
		savingFlux.buffer().blockFirst();
	}

	@Test
	void getAllLogsTest() {
		List<LogDto> list = queryListLogDto(pathGetAllLogs);
		assertEquals(18, list.size());
	}

	@Test
	void getLogsByTypeTest() {
		Arrays.stream(LogType.values()).forEach(logType -> {
			List<LogDto> list = queryListLogDto(pathGetLogsByTypeBase + logType);
			list.forEach(logDto -> assertEquals(logDto.logType, logType));
			log.debug("===> Test: asserted query for logType {}. Size of the responds list {}.", logType, list.size());
		});
	}

	@Test
	void getLogsAllExceptionsTest() {
		List<LogDto> list = queryListLogDto(pathGetAllExceptions);
		list.forEach(logDto -> assertNotEquals(logDto, LogType.NO_EXCEPTION));
		log.debug("===> Test: asserted query for all exceptions. Size of the responds list {}.", list.size());
	}

	private List<LogDto> queryListLogDto(String path) {
		return webTestClient.get()
				.uri(path).exchange()
				.expectStatus().isOk()
				.expectBodyList(LogDto.class)
				.returnResult()
				.getResponseBody();
	}

	@Test
	void getDistribution() {
		LogTypeCount[] listExp = {
				new LogTypeCount(AUTHENTICATION_EXCEPTION, 5), 
				new LogTypeCount(AUTHORIZATION_EXCEPTION, 4), 
				new LogTypeCount(BAD_REQUEST_EXCEPTION, 3), 
				new LogTypeCount(NO_EXCEPTION, 3),
				new LogTypeCount(SERVER_EXCEPTION, 1),
				new LogTypeCount(DUPLICATED_KEY_EXCEPTION, 1), 
				new LogTypeCount(NOT_FOUND_EXCEPTION, 1)
		};
		
		executeTest("/logs/distribution", LogTypeCount[].class, listExp);
	}

	@Test
	void getMostEncounteredExceptionTypes() {
		LogType[] expected = {AUTHENTICATION_EXCEPTION, AUTHORIZATION_EXCEPTION};
		executeTest("/logs/mostencountered_exception_types?n_types=2", LogType[].class, expected);
	}

	@Test
	void getArtifactsDistribution() {
		ArtifactCount[] listExp = {
				new ArtifactCount("class5", 5), 
				new ArtifactCount("class4", 4), 
				new ArtifactCount("class3", 3), 
				new ArtifactCount("artifact", 3), 
				new ArtifactCount("class2", 2), 
				new ArtifactCount("class1", 1)};
		executeTest("/logs/artifacts_distribution", ArtifactCount[].class, listExp);
	}
	
	@Test
	void getMostEencounteredAartifacts() {
		String[] listExp = {"class5", "class4"};
		executeTest("/logs/mostencountered_artifacts?n_artifacts=2", String[].class, listExp);
	}
	
	private <T> void executeTest(String path, Class<T[]> clazz, T[] expected) {
		 webTestClient.get()
				.uri(path)
				.exchange().expectStatus().isOk()
				.expectBody(clazz)
				.isEqualTo(expected);
	}
}
