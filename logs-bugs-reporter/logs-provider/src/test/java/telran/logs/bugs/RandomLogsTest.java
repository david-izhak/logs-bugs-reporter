package telran.logs.bugs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
@Slf4j
public class RandomLogsTest {
	@Value("${app-N_LOGS_SENT:10}")
	private int N_LOGS_SENT;

	@Autowired
	OutputDestination output;

	@Test
	void sendRandomLogs() throws InterruptedException {
		Set<String> messageStrSet = new HashSet<>();
		for (int i = 0; i < N_LOGS_SENT; i++) {
			Message<byte[]> recivedMessage = null;
			while (recivedMessage == null) {
				recivedMessage = output.receive();
			}
			byte[] messageBytes = recivedMessage.getPayload();
			String messageStr = new String(messageBytes);
			messageStrSet.add(messageStr);
			log.debug("received in test: {}", messageStr);
 		}
		assertEquals(N_LOGS_SENT, messageStrSet.size());
	}
}
