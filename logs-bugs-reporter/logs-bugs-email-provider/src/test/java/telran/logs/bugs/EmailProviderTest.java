package telran.logs.bugs;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
@AutoConfigureWebTestClient
@AutoConfigureDataJpa
public class EmailProviderTest {
	
	final String INIT_SQL = "fillTables.sql";

	@Value("${email-provider-test-email}")
	String expectedEmail;
	
	@Value("${email-provider-test-path-exist}")
	String	pathWithExistArtifact;

	@Value("${email-provider-test-path-no-exist}")
	String	pathWithNoExistArtifact;
	
	@Autowired
	WebTestClient webTestClient;
	
	@Test
	@Sql(INIT_SQL)
	void emailExisting() {
		webTestClient.get().uri(pathWithExistArtifact)
		.exchange().expectStatus().isOk().expectBody(String.class).isEqualTo(expectedEmail);
	}
	
	@Test
	@Sql(INIT_SQL)
	void emailNotExisting() {
		webTestClient.get().uri(pathWithNoExistArtifact)
		.exchange().expectStatus().isOk().expectBody(String.class).isEqualTo("");
	}
}
