package telran.logs.bugs;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class EmailProviderTest {
	
	@Value("${assigner-provider-email:moshe@mail.com}")
	String expectedEmail;
	
	@Value("${assiner-email-provider-test-path:/email/assigner}")
	String	path;
	
	@Autowired
	WebTestClient webTestClient;
	
	@Test
	void emailExisting() {
		webTestClient.get().uri(path).exchange().expectStatus().isOk().expectBody(String.class).isEqualTo(expectedEmail);
	}
}
