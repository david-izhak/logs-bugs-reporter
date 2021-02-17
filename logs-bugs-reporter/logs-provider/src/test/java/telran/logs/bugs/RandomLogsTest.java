package telran.logs.bugs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.test.web.reactive.server.WebTestClient;

import lombok.extern.slf4j.Slf4j;

import telran.logs.bugs.dto.*;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
@Slf4j
public class RandomLogsTest {
	
	@Value("${app-AUTHENTICATION_ARTIFACT:authentication}")
	private String AUTHENTICATION_ARTIFACT;
	@Value("${app-AUTHORIZATION_ARTIFACT:authorization}")
	private String AUTHORIZATION_ARTIFACT;
	@Value("${app-CLASS_ARTIFACT:class}")
	private String CLASS_ARTIFACT;
	@Value("${app-N_LOGS:100000}")
	private long N_LOGS;
	@Value("${app-N_LOGS_SENT:10}")
	private int N_LOGS_SENT;

	@Autowired
	RandomLogs randomLogs;

	@Autowired
	OutputDestination output;
	
	@Test
	void logTypeArtifactTest() throws Exception {
		EnumMap<LogType, String> logTypeArtifactsMap = getMapForTest();
		logTypeArtifactsMap.forEach((k, v) -> {
			switch (k) {
			case AUTHENTICATION_EXCEPTION:
				assertEquals(AUTHENTICATION_ARTIFACT, v);
				break;
			case AUTHORIZATION_EXCEPTION:
				assertEquals(AUTHORIZATION_ARTIFACT, v);
				break;
			default:
				testClassArtifact(v);
			}
		});
	}

	private void testClassArtifact(String artifact) {
		assertEquals(CLASS_ARTIFACT, artifact.substring(0, 5));
		int classNumber = Integer.parseInt(artifact.substring(5));
		assertTrue(classNumber >= 1 && classNumber <= randomLogs.nClasses);
	}

	private EnumMap<LogType, String> getMapForTest()
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Method getMapMethod = randomLogs.getClass().getDeclaredMethod("getLogArtifactMap");
		getMapMethod.setAccessible(true);
		@SuppressWarnings("unchecked")
		EnumMap<LogType, String> logTypeArtifactsMap = (EnumMap<LogType, String>) getMapMethod.invoke(randomLogs);
		return logTypeArtifactsMap;
	}

	@DisplayName("Show count of each LogType after random generation")
	@Test
	void generation() throws Exception {
		List<LogDto> logs = Stream.generate(() -> randomLogs.createRandomLog()).limit(N_LOGS)
				.collect(Collectors.toList());
		testLogContent(logs);
		Map<LogType, Long> logTypeOccurrences = logs.stream()
				.collect(Collectors.groupingBy(l -> l.logType, Collectors.counting()));
		log.info("Statistic **********************************");
		logTypeOccurrences.forEach((k, v) -> {
			String str = String.format("LogType: %s. Count: %d", k, v);
			log.info(str);
		});
		log.info("**********************************");
	}

	private void testLogContent(List<LogDto> logs) {
		logs.forEach(logFromList -> {
			switch (logFromList.logType) {
			case AUTHENTICATION_EXCEPTION:
				assertEquals(AUTHENTICATION_ARTIFACT, logFromList.artifact);
				assertEquals(0, logFromList.responseTime);
				assertTrue(logFromList.result.isEmpty());
				break;
			case AUTHORIZATION_EXCEPTION:
				assertEquals(AUTHORIZATION_ARTIFACT, logFromList.artifact);
				assertEquals(0, logFromList.responseTime);
				assertTrue(logFromList.result.isEmpty());
				break;
			case NO_EXCEPTION:
				testClassArtifact(logFromList.artifact);
				assertTrue(logFromList.responseTime > 0);
				assertTrue(logFromList.result.isEmpty());
				break;
			default:
				testClassArtifact(logFromList.artifact);
				assertEquals(0, logFromList.responseTime);
				assertTrue(logFromList.result.isEmpty());
				break;
			}
		});
	}

	@Test
	void sendRandomLogs() throws InterruptedException {
		Set<String> messageStrSet = new HashSet<>();
		for (int i = 0; i < N_LOGS_SENT; i++) {
			Message<byte[]> recivedMessage = null;
			while (recivedMessage == null) {
				recivedMessage = output.receive(Long.MAX_VALUE);
			}
			byte[] messageBytes = recivedMessage.getPayload();
			String messageStr = new String(messageBytes);
			messageStrSet.add(messageStr);
			log.debug("received in test: {}", messageStr);
 		}
		assertEquals(N_LOGS_SENT, messageStrSet.size());
	}
}
