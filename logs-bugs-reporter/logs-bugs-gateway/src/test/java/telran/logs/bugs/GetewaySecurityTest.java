package telran.logs.bugs;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

import lombok.extern.log4j.Log4j2;

@SpringBootTest(classes = LogsBugsGatewayAppl.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Log4j2
public class GetewaySecurityTest {
	
	@Autowired
	WebTestClient webTestClient;
	
	@Test
	void contextLoads() {
		assertThat(webTestClient).isNotNull();
	}
	
	@Nested
	@DisplayName("Authorization")
	class Authorization {
		
		@Nested
		class Authorized {
			
			@Nested
			class WithoutProperRol {

				@WithMockUser
				@Test
				void getLogs() {
					webTestClient.get()
					.uri("/logs-info-back-office/logs").exchange()
					.expectStatus().isForbidden();
				}

				@WithMockUser
				@Test
				void getLogsByType() {
					webTestClient.get()
					.uri("/logs-info-back-office/logs/type?type=NOT_FOUND_EXCEPTION").exchange()
					.expectStatus().isForbidden();
				}

				@WithMockUser
				@Test
				void openBugs() {
					webTestClient.get()
					.uri("/reporter-back-office/bugs/open").exchange()
					.expectStatus().isForbidden();
				}

				@WithMockUser
				@Test
				void openAassignBugs() {
					webTestClient.get()
					.uri("/reporter-back-office/bugs/open/assign").exchange()
					.expectStatus().isForbidden();
				}

				@WithMockUser
				@Test
				void assignBugs() {
					webTestClient.get()
					.uri("/reporter-back-office/bugs/assign").exchange()
					.expectStatus().isForbidden();
				}
				
				@WithMockUser
				@Test
				void closeBug() {
					webTestClient.get()
					.uri("/reporter-back-office/bugs/close_bug").exchange()
					.expectStatus().isForbidden();
				}
				
				@WithMockUser
				@Test
				void addProgrammers() {
					webTestClient.post()
					.uri("/reporter-back-office/bugs/programmers").exchange()
					.expectStatus().isForbidden();
				}
				
				@WithMockUser
				@Test
				void addArtifact() {
					webTestClient.post()
					.uri("/reporter-back-office/bugs/artifact").exchange()
					.expectStatus().isForbidden();
				}
				
				@WithMockUser
				@Test
				void getNonassigned() {
					webTestClient.get()
					.uri("/reporter-back-office/bugs/nonassigned").exchange()
					.expectStatus().isNotFound();
				}

				@WithMockUser
				@Test
				void getProgrammerBugs() {
					webTestClient.get()
					.uri("/reporter-back-office/bugs/programmers?programmer_id=100").exchange()
					.expectStatus().isNotFound();
				}

				@WithMockUser
				@Test
				void getProgrammersBugsCount() {
					webTestClient.get()
					.uri("/reporter-back-office/bugs/programmers/count").exchange()
					.expectStatus().isNotFound();
				}
			}
			
			@Nested
			class WithProperRol {

				@WithMockUser(roles = {"DEVELOPER"})
				@Test
				void getLogsWithProperRoleDevelopeR() {
					webTestClient.get()
					.uri("/logs-info-back-office/logs").exchange()
					.expectStatus().isNotFound();
				}

				@WithMockUser(roles = {"DEVELOPER"})
				@Test
				void getLogsByTypeWithProperRoleDeveloper() {
					webTestClient.get()
					.uri("/logs-info-back-office/logs/type?type=NOT_FOUND_EXCEPTION").exchange()
					.expectStatus().isNotFound();
				}

				@WithMockUser(roles = {"ASSIGNER"})
				@Test
				void openBugsWithProperRoleAssigner() {
					webTestClient.get()
					.uri("/reporter-back-office/bugs/open").exchange()
					.expectStatus().isNotFound();
				}
				
				@WithMockUser(roles = {"TESTER"})
				@Test
				void openBugsWithProperRoleTester() {
					webTestClient.get()
					.uri("/reporter-back-office/bugs/open").exchange()
					.expectStatus().isNotFound();
				}
				
				@WithMockUser(roles = {"DEVELOPER"})
				@Test
				void openBugsWithProperRoleDeveloper() {
					webTestClient.get()
					.uri("/reporter-back-office/bugs/open").exchange()
					.expectStatus().isNotFound();
				}

				@WithMockUser(roles = {"ASSIGNER"})
				@Test
				void openAassignBugsWithProperRoleAssigner() {
					webTestClient.get()
					.uri("/reporter-back-office/bugs/open/assign").exchange()
					.expectStatus().isNotFound();
				}
				
				@WithMockUser(roles = {"TESTER"})
				@Test
				void openAassignBugsWithProperRoleTester() {
					webTestClient.get()
					.uri("/reporter-back-office/bugs/open/assign").exchange()
					.expectStatus().isNotFound();
				}
				
				@WithMockUser(roles = {"DEVELOPER"})
				@Test
				void openAassignBugsWithProperRoleDeveloper() {
					webTestClient.get()
					.uri("/reporter-back-office/bugs/open/assign").exchange()
					.expectStatus().isNotFound();
				}

				@WithMockUser(roles = {"ASSIGNER"})
				@Test
				void assignBugsWithProperRoleAssigner() {
					webTestClient.get()
					.uri("/reporter-back-office/bugs/assign").exchange()
					.expectStatus().isNotFound();
				}
				
				@WithMockUser(roles = {"TESTER"})
				@Test
				void closeBugWithProperRoleTester() {
					webTestClient.get()
					.uri("/reporter-back-office/bugs/close_bug").exchange()
					.expectStatus().isNotFound();
				}
				
				@WithMockUser(roles = {"PROJECT_OWNER"})
				@Test
				void addProgrammersWithProperRoleProject_Owner() {
					webTestClient.post()
					.uri("/reporter-back-office/bugs/programmers").exchange()
					.expectStatus().isNotFound();
				}
				
				@WithMockUser(roles = {"TEAM_LEAD"})
				@Test
				void addArtifactWithProperRoleTeam_Lead() {
					webTestClient.post()
					.uri("/reporter-back-office/bugs/artifact").exchange()
					.expectStatus().isNotFound();
				}
				
				@WithMockUser(roles = {"ASSIGNER"})
				@Test
				void addArtifactWithProperRoleAssigner() {
					webTestClient.post()
					.uri("/reporter-back-office/bugs/artifact").exchange()
					.expectStatus().isNotFound();
				}
			}
		}
		
		@Nested
		class Unauthorized {
			@Test
			void getLogsUnauthorized() {
				webTestClient.get()
				.uri("/logs-info-back-office/logs").exchange()
				.expectStatus().isUnauthorized();
			}

			@Test
			void getLogsByTypeUnauthorized() {
				webTestClient.get()
				.uri("/logs-info-back-office/logs/type?type=NOT_FOUND_EXCEPTION").exchange()
				.expectStatus().isUnauthorized();
			}

			@Test
			void openBugsUnauthorized() {
				webTestClient.get()
				.uri("/reporter-back-office/bugs/open").exchange()
				.expectStatus().isUnauthorized();
			}

			@Test
			void openAassignBugsUnauthorized() {
				webTestClient.get()
				.uri("/reporter-back-office/bugs/open/assign").exchange()
				.expectStatus().isUnauthorized();
			}

			@Test
			void getNonassignedUnauthorized() {
				webTestClient.get()
				.uri("/reporter-back-office/bugs/nonassigned").exchange()
				.expectStatus().isUnauthorized();
			}

			@Test
			void assignBugsUnauthorized() {
				webTestClient.get()
				.uri("/reporter-back-office/bugs/assign").exchange()
				.expectStatus().isUnauthorized();
			}
			
			@Test
			void closeBugUnauthorized() {
				webTestClient.get()
				.uri("/reporter-back-office/bugs/close_bug").exchange()
				.expectStatus().isUnauthorized();
			}
			
			@Test
			void addProgrammersUnauthorized() {
				webTestClient.post()
				.uri("/reporter-back-office/bugs/programmers").exchange()
				.expectStatus().isUnauthorized();
			}
			
			@Test
			void addArtifactUnauthorized() {
				webTestClient.post()
				.uri("/reporter-back-office/bugs/artifact").exchange()
				.expectStatus().isUnauthorized();
			}
			
			@Test
			void getProgrammerBugsUnauthorized() {
				webTestClient.get()
				.uri("/reporter-back-office/bugs/programmers?programmer_id=100").exchange()
				.expectStatus().isUnauthorized();
			}
			
			@Test
			void getProgrammersBugsCountUnauthorized() {
				webTestClient.get()
				.uri("/reporter-back-office/bugs/programmers/count").exchange()
				.expectStatus().isUnauthorized();
			}
		}
		
	}
	
}
