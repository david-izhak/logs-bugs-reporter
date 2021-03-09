package telran.logs.bugs;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.reactive.server.WebTestClient;

import telran.logs.bugs.api.AssignerMailProviderApi;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class AssignerMailProviderTest implements AssignerMailProviderApi {
	@Autowired
	WebTestClient webTestClient;
	
	@Test
	void emailExisting() {
		webTestClient.get()
		.uri(PATH_EMAIL_ASSIGNER).exchange()
		.expectStatus().isOk()
		.expectBody(String.class)
		.isEqualTo(DEFOULT_ASSIGNER_EMAIL);
	}
}
