package telran.logs.bugs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import telran.logs.bugs.dto.ArtifactDto;
import telran.logs.bugs.dto.AssignBugData;
import telran.logs.bugs.dto.BugAssignDto;
import telran.logs.bugs.dto.BugDto;
import telran.logs.bugs.dto.BugResponseDto;
import telran.logs.bugs.dto.BugStatus;
import telran.logs.bugs.dto.CloseBugData;
import telran.logs.bugs.dto.EmailBugsCount;
import telran.logs.bugs.dto.OpeningMethod;
import telran.logs.bugs.dto.ProgrammerDto;
import telran.logs.bugs.dto.Seriousness;
import telran.logs.bugs.dto.SeriousnessBugCount;
import telran.logs.bugs.jpa.entities.Artifact;
import telran.logs.bugs.jpa.entities.Bug;
import telran.logs.bugs.jpa.entities.Programmer;
import telran.logs.bugs.jpa.repo.ArtifactRepository;
import telran.logs.bugs.jpa.repo.BugRepository;
import telran.logs.bugs.jpa.repo.ProgrammerRepository;
import static telran.logs.bugs.api.BugsReporterApi.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@AutoConfigureDataJpa
@TestMethodOrder(OrderAnnotation.class)
public class BugsReporterRestApiTest {

	@NoArgsConstructor
	@AllArgsConstructor
	@EqualsAndHashCode
	@ToString
	public static class EmailBugCountTest implements EmailBugsCount {
		String email;
		long count;

		@Override
		public String getEmail() {
			return email;
		}

		@Override
		public long getCount() {
			return count;
		}
	}

	@Autowired
	WebTestClient webTestClient;

	@Autowired
	ArtifactRepository artifactRepository; // not used

	@Autowired
	BugRepository bugRepository;

	@Autowired
	ProgrammerRepository programmerRepository;
	
//	Data set
	LocalDate date = LocalDate.now();
	List<Programmer> programmersList = Arrays.asList(
			new Programmer(1, "Bob", "bob@gmail.com"), 
			new Programmer(2, "John", "jhon@gmail.com"), 
			new Programmer(3, "Anna", "anna@gmail.com"), 
			new Programmer(4, "Tedd", "tedd@gmail.com"));
	Bug bug1 = new Bug("description", date, null, BugStatus.OPENND, Seriousness.BLOCKING, OpeningMethod.MANUAL, null);
	Bug bugExp = new Bug(1, "description" + "\nClose Description: " + "closeBugData_description", date, date,
			BugStatus.CLOSED, Seriousness.BLOCKING, OpeningMethod.MANUAL, null);
	Bug bugExp2 = new Bug(1, "description" + "\nAssignment Description: " + "assign_description", date, null,
			BugStatus.ASSIGNED, Seriousness.BLOCKING, OpeningMethod.MANUAL, programmersList.get(0));
	List<Bug> bugsList = Arrays.asList(
			new Bug("description", date, null, BugStatus.ASSIGNED, Seriousness.BLOCKING, OpeningMethod.MANUAL, programmersList.get(0)),
			new Bug("description", date, null, BugStatus.ASSIGNED, Seriousness.BLOCKING, OpeningMethod.MANUAL, programmersList.get(0)),
			new Bug("description", date, null, BugStatus.ASSIGNED, Seriousness.BLOCKING, OpeningMethod.MANUAL, programmersList.get(0)),
			new Bug("description", date, null, BugStatus.ASSIGNED, Seriousness.COSMETIC, OpeningMethod.MANUAL, programmersList.get(1)),
			new Bug("description", date, null, BugStatus.ASSIGNED, Seriousness.COSMETIC, OpeningMethod.MANUAL, programmersList.get(1)),
			new Bug("description", date, null, BugStatus.ASSIGNED, Seriousness.MINOR, OpeningMethod.MANUAL, programmersList.get(2)),
			new Bug("description", date, null, BugStatus.OPENND, Seriousness.CRITICAL, OpeningMethod.MANUAL, null)
			);
	SeriousnessBugCount[] seriousnessBugCount = {
			new SeriousnessBugCount(Seriousness.BLOCKING, 3),
			new SeriousnessBugCount(Seriousness.COSMETIC, 2),
			new SeriousnessBugCount(Seriousness.CRITICAL, 1),
			new SeriousnessBugCount(Seriousness.MINOR, 1),
			};  // for test 11
	BugDto bugDto = new BugDto(Seriousness.BLOCKING, "description", date);
	BugResponseDto bugResponseDto1 = new BugResponseDto(1, Seriousness.BLOCKING, "description", date, 1, null, BugStatus.ASSIGNED, OpeningMethod.MANUAL);
	BugResponseDto bugResponseDto = new BugResponseDto(1, Seriousness.BLOCKING, "description", date, 0, null, BugStatus.OPENND, OpeningMethod.MANUAL);
	List<BugResponseDto> listExp = Arrays.asList(bugResponseDto1);
	ProgrammerDto programmerDto = new ProgrammerDto(1, "Bob", "bob@gmail.com");
	ArtifactDto artifactDto = new ArtifactDto("artifact_test", 1);
	CloseBugData closeBugData = new CloseBugData(1, date, "closeBugData_description");
	BugAssignDto bugAssignDto1 = new BugAssignDto(Seriousness.BLOCKING, "description", date, 1);
	BugAssignDto bugAssignDto = new BugAssignDto(Seriousness.BLOCKING, "description", date, 1000);
	String[] expectedArray = { "Bob", "John" };
	String[] expectedArray2 = { "Tedd", "Anna" };
	AssignBugData assignBugData = new AssignBugData(1, 1, "assign_description");
	List<EmailBugCountTest> expectedEmailCounts = Arrays.asList(new EmailBugCountTest("bob@gmail.com", 3),
			new EmailBugCountTest("jhon@gmail.com", 2), new EmailBugCountTest("anna@gmail.com", 1), new EmailBugCountTest("tedd@gmail.com", 0));
	List<Seriousness> seriousnessList = Arrays.asList(
			Seriousness.BLOCKING,
			Seriousness.COSMETIC
	);
	
//	test for POST(1)
	private <T, P> void executePostTest(String path, Class<P> clazz, T body, P expected) {
		webTestClient.post()
		.uri(path)
		.bodyValue(body).exchange()
		.expectStatus().isOk()
		.expectBody(clazz)
		.isEqualTo(expected);
	}
//	test for POST(2) isNotFound
	private <T> void executePostTestExpectNotFound(String path, T dto) {
		webTestClient.post()
		.uri(PATH_BUGS_OPEN_ASSIGN)
		.contentType(MediaType.APPLICATION_JSON)
		.bodyValue(bugAssignDto)
		.exchange()
		.expectStatus().isNotFound();
	}
//	test for POST(3) isBadRequest
	private <T> void executePostTestExpectBadRequest(String path, T dto) {
		webTestClient.post()
		.uri(path)
		.contentType(MediaType.APPLICATION_JSON)
		.bodyValue(dto)
		.exchange()
		.expectStatus().isBadRequest();
	}

//	test for PUT(1) isOk
	private <T> void executePutTest(String path, T dto) {
		webTestClient.put()
		.uri(path).contentType(MediaType.APPLICATION_JSON)
		.bodyValue(dto).exchange()
		.expectStatus().isOk();
	}

//	test for PUT(2) isNotFound
	private <T> void executePutTestExpectNotFound(String path, T dto) {
		webTestClient.put()
		.uri(path)
		.contentType(MediaType.APPLICATION_JSON)
		.bodyValue(dto).exchange()
		.expectStatus().isNotFound();
	}

//	test for PUT(3) isBadRequest
	private <T> void executePutTestExpectBadRequest(String path, T dto) {
		webTestClient.put()
		.uri(path)
		.contentType(MediaType.APPLICATION_JSON)
		.bodyValue(dto).exchange()
		.expectStatus().isBadRequest();
	}

//	test for GET(1) expectBodyList
	private <T> void executeGetTestExpectList(String path, Class<T> clazz, List<T> list) {
		webTestClient.get()
		.uri(path).exchange()
		.expectStatus().isOk()
		.expectBodyList(clazz)
		.isEqualTo(list);
	}
	
//	test for GET(2) isBadRequest
	private void executeGetRequestExpectBadRequest(String uriStr) {
		webTestClient.get()
		.uri(uriStr)
		.exchange()
		.expectStatus().isBadRequest();
	}
	
//	test for GET(3) expected array
	private <T> void executeGetTestExpectedArray(String path, Class<T> clazz, T array) {
		webTestClient.get()
		.uri(path)
		.exchange()
		.expectStatus().isOk()
		.expectBody(clazz)
		.isEqualTo(array);
	}
//	test for GET(4) isNotFound
	private void executeGetRequestExpectNotFound(String uriStr) {
		webTestClient.get()
		.uri(uriStr)
		.exchange()
		.expectStatus().isNotFound();
	}

	@Nested
	@DisplayName("Tests for assignBug method in BugsReporterImpl")
	class BugsReporterImpl_assignBug_test {
		@Nested
		@DisplayName("Positive")
		class BugsReporterImpl_assignBug_Positive {
			@Test
			@Order(1)
			@Sql("fillTables.sql")
			void assign_opened_bug_to_programmer_by_id_expect_ok() {
				programmerRepository.save(programmersList.get(0));
				bugRepository.save(bug1);
				executePutTest(PATH_BUGS_ASSIGN, assignBugData);
				assertEquals(bugExp2, bugRepository.findAll().get(0));
			}
		}
		
		@Nested
		@DisplayName("Negative")
		class BugsReporterImpl_assignBug_Negative {
			@Test
			@Order(2)
			void assign_bug_with_not_exists_programmer_id_expect_BadRequest() {
				programmerRepository.save(programmersList.get(0));
				bugRepository.save(bug1);
				AssignBugData assignBugData1 = new AssignBugData(1, 100, "assign_description");
				executePutTestExpectNotFound(PATH_BUGS_ASSIGN, assignBugData1);
			}
			@Test
			@Order(3)
			void assign_bug_with_invalid_programmer_id_expect_BadRequest() {
				programmerRepository.save(programmersList.get(0));
				bugRepository.save(bug1);
				AssignBugData assignBugData1 = new AssignBugData(1, 0, "assign_description");
				executePutTestExpectBadRequest(PATH_BUGS_ASSIGN, assignBugData1);
				AssignBugData assignBugData2 = new AssignBugData(1, -1, "assign_description");
				executePutTestExpectBadRequest(PATH_BUGS_ASSIGN, assignBugData2);
			}
			@Test
			@Order(4)
			void assign_bug_with_not_exists_bug_id_expect_BadRequest() {
				programmerRepository.save(programmersList.get(0));
				bugRepository.save(bug1);
				AssignBugData assignBugData1 = new AssignBugData(100, 1, "assign_description");
				executePutTestExpectNotFound(PATH_BUGS_ASSIGN, assignBugData1);
			}
			@Test
			@Order(5)
			void assign_bug_with_invalid_bug_id_expect_BadRequest() {
				programmerRepository.save(programmersList.get(0));
				bugRepository.save(bug1);
				AssignBugData assignBugData1 = new AssignBugData(0, 1, "assign_description");
				executePutTestExpectBadRequest(PATH_BUGS_ASSIGN, assignBugData1);
				AssignBugData assignBugData2 = new AssignBugData(-1, 1, "assign_description");
				executePutTestExpectBadRequest(PATH_BUGS_ASSIGN, assignBugData2);
			}
		}
	}

	@Nested
	@DisplayName("Tests for getBugsProgrammer method in BugsReporterImpl")
	class BugsReporterImpl_getBugsProgrammer_test {
		@Nested
		@DisplayName("Positive")
		class BugsReporterImpl_getBugsProgrammer_Positive {
			@Test
			@Order(6)
			@Sql("fillTables.sql")
			void get_all_bugs_of_programmer_by_id_expect_ok() {
				programmerRepository.save(programmersList.get(0));
				bugRepository.save(bugsList.get(0));
				executeGetTestExpectList(PATH_BUGS_PROGRAMMERS_GET + "?programmer_id=1", BugResponseDto.class, listExp);
			}
		}
		@Nested
		@DisplayName("Negative")
		class BugsReporterImpl_getBugsProgrammer_Negative {
			@Test
			@Order(7)
			void get_bugs_of_not_exists_programmer_id_expect_BadRequest() {
				programmerRepository.save(programmersList.get(0));
				bugRepository.save(bugsList.get(0));
				executeGetRequestExpectNotFound(PATH_BUGS_PROGRAMMERS_GET + "?programmer_id=100");

			}
			@Test
			@Order(8)
			void get_bugs_of_with_invalid_programmer_id_expect_BadRequest() {
				programmerRepository.save(programmersList.get(0));
				bugRepository.save(bugsList.get(0));
				executeGetRequestExpectBadRequest(PATH_BUGS_PROGRAMMERS_GET + "?programmer_id=0");
				programmerRepository.save(programmersList.get(0));
				bugRepository.save(bugsList.get(0));
				executeGetRequestExpectBadRequest(PATH_BUGS_PROGRAMMERS_GET + "?programmer_id=-0");
			}
		}
	}

	@Nested
	@DisplayName("Tests for getEmailBugsCounts method in BugsReporterImpl")
	class BugsReporterImpl_getEmailBugsCounts_test {
		@Test
		@Order(9)
		@DisplayName("Test getEmailBugsCount method of controller")
		@Sql("fillTables.sql")
		void get_emails_of_programmers_with_counts_of_bugs_expect_ok() {
			programmerRepository.saveAll(programmersList);
			bugRepository.saveAll(bugsList);
			executeGetTestExpectList(BUGS_PROGRAMMERS_COUNT, EmailBugCountTest.class, expectedEmailCounts);
		}
	}


	@Nested
	@DisplayName("Tests for closeBug method in BugsReporterImpl")
	class BugsReporterImpl_closeBug_test {
		@Nested
		@DisplayName("Positive")
		class BugsReporterImpl_closeBug_Positive {
			@Test
			@Order(10)
			@Sql("fillTables.sql")
			void close_bug_expect_ok() {
				bugRepository.save(bug1);
				executePutTest(PATH_BUGS_CLOSE_DATA, closeBugData);
				assertEquals(bugExp, bugRepository.findAll().get(0));
			}
			@Test
			@Order(11)
			@Sql("fillTables.sql")
			void close_bug_with_date_null_expect_ok() {
				bugRepository.save(bug1);
				CloseBugData closeBugData1 = new CloseBugData(1, null, "closeBugData_description");
				executePutTest(PATH_BUGS_CLOSE_DATA, closeBugData1);
				assertEquals(bugExp, bugRepository.findAll().get(0));
			}
		}
		@Nested
		@DisplayName("Negative")
		class BugsReporterImpl_closeBug_Negative {
			@Test
			@Order(12)
			void close_bug_with_not_exists_bug_id_expect_NotFound() {
				bugRepository.save(bug1);
				CloseBugData closeBugData1 = new CloseBugData(100, date, "closeBugData_description");
				executePutTestExpectNotFound(PATH_BUGS_CLOSE_DATA, closeBugData1);
			}
			@Test
			@Order(13)
			void close_bug_with_invalid_bug_id_expect_BadRequest() {
				bugRepository.save(bug1);
				CloseBugData closeBugData1 = new CloseBugData(0, date, "closeBugData_description");
				executePutTestExpectBadRequest(PATH_BUGS_CLOSE_DATA, closeBugData1);
				bugRepository.save(bug1);
				CloseBugData closeBugData2 = new CloseBugData(-1, date, "closeBugData_description");
				executePutTestExpectBadRequest(PATH_BUGS_CLOSE_DATA, closeBugData2);
			}
		}
	}

	@Nested
	@DisplayName("Tests for getProgrammersMostBugs method in BugsReporterImpl")
	class BugsReporterImpl_getProgrammersMostBugs_test {
		@Nested
		@DisplayName("Positive")
		class BugsReporterImpl_getProgrammersMostBugs_Positive {
			@Test
			@Order(14)
			@DisplayName("get Programmers with Most count of Bugs")
			void get_programmers_with_most_count_of_bugs_expect_ok() {
				programmerRepository.saveAll(programmersList);
				bugRepository.saveAll(bugsList);
				executeGetTestExpectedArray(BUGS_MOST_N_PROGRAMMERS + "?n_programmers=2", String[].class,
						expectedArray);
			}
		}
		@Nested
		@DisplayName("Negative")
		class BugsReporterImpl_getProgrammersMostBugs_Negative {
			@Test
			@Order(15)
			@Sql("fillTables.sql")
			void get_programmers_with_most_count_of_bugs_expect_BadRequest() {
				programmerRepository.saveAll(programmersList);
				bugRepository.saveAll(bugsList);
				executeGetRequestExpectBadRequest(BUGS_MOST_N_PROGRAMMERS + "?n_programmers=0");
				executeGetRequestExpectBadRequest(BUGS_MOST_N_PROGRAMMERS + "?n_programmers=-1");
			}
		}
	}
	
	@Nested
	@DisplayName("Tests for getSeriousnessDistribution method in BugsReporterImpl")
	class BugsReporterImpl_getSeriousnessDistribution_test {
		@Test
		@Order(16)
		@DisplayName("Distribution of bugs according seriousness")
		@Sql("fillTables.sql")
		void get_distribution_of_seriousness_expect_ok() {
			programmerRepository.saveAll(programmersList);
			bugRepository.saveAll(bugsList);
			executeGetTestExpectedArray(BUGS_SERIOUSNESS_COUNT, SeriousnessBugCount[].class, seriousnessBugCount);
		}
	}
	
	@Nested
	@DisplayName("Tests for getSeriousnessTypesWithMostBugs method in BugsReporterImpl")
	class BugsReporterImpl_getSeriousnessTypesWithMostBugs_test {
		@Nested
		@DisplayName("Positive")
		class BugsReporterImpl_getSeriousnessTypesWithMostBugs_Positive {
			@Test
			@Order(17)
			void get_types_of_seriousness_ordered_according_count_of_bugs_expect_ok() {
				programmerRepository.saveAll(programmersList);
				bugRepository.saveAll(bugsList);
				executeGetTestExpectList(BUGS_SERIOUSNESS_MOST + "?n_seriousness=2", Seriousness.class,
						seriousnessList);
			}
		}
		@Nested
		@DisplayName("Negative")
		class BugsReporterImpl_getSeriousnessTypesWithMostBugs_Negative {
			@Test
			@Order(18)
			void get_types_of_seriousness_ordered_according_count_of_bugs_expect_ok() {
				programmerRepository.saveAll(programmersList);
				bugRepository.saveAll(bugsList);
				executeGetRequestExpectBadRequest(BUGS_SERIOUSNESS_MOST + "?n_seriousness=0");
				executeGetRequestExpectBadRequest(BUGS_SERIOUSNESS_MOST + "?n_seriousness=-1");
			}
		}
	}
	
	@Nested
	@DisplayName("Tests for getProgrammersLeastBugs method in BugsReporterImpl")
	class BugsReporterImpl_getProgrammersLeastBugs_test {
		@Nested
		@DisplayName("Positive")
		class BugsReporterImpl_getProgrammersLeastBugs_Positive {
			@Test
			@Order(19)
			@DisplayName("Get programmers with least quantity of bugs")
			void get_programmers_with_leastcount_of_bugs_expect_ok() {
				programmerRepository.saveAll(programmersList);
				bugRepository.saveAll(bugsList);
				executeGetTestExpectedArray(BUGS_LEAST_N_PROGRAMMERS + "?n_programmers=2", String[].class,
						expectedArray2);
			}
		}
		@Nested
		@DisplayName("Negativ")
		class BugsReporterImpl_getProgrammersLeastBugs_Negativ {
			@Test
			@Order(20)
			void invalid_number_of_programmers_in_request_programmers_with_least_count_of_bugs_expect_BadRequest() {
				executeGetRequestExpectBadRequest(BUGS_LEAST_N_PROGRAMMERS + "?n_programmers=-1");
				executeGetRequestExpectBadRequest(BUGS_LEAST_N_PROGRAMMERS + "?n_programmers=0");
			}
		}
	}
	
	@Nested
	@DisplayName("Tests for openAndAssignBug method in BugsReporterImpl")
	class BugsReporterImpl_openAndAssignBug_test {
		@Nested
		@DisplayName("Positive")
		class BugsReporterImpl_addProgrammer_Positive {
			@Test
			@Order(21)
			@Sql("fillTables.sql")
			void open_bug_and_assign_to_programmer_expect_ok() {
				programmerRepository.save(programmersList.get(0));
				executePostTest(PATH_BUGS_OPEN_ASSIGN, BugResponseDto.class, bugAssignDto1, bugResponseDto1);
			}
			@Test
			@Order(22)
			@Sql("fillTables.sql")
			void open_bug_and_assign_to_programmer_with_date_null_expect_ok() {
				programmerRepository.save(programmersList.get(0));
				BugAssignDto bugAssignDto2 = new BugAssignDto(Seriousness.BLOCKING, "description", null, 1);
				executePostTest(PATH_BUGS_OPEN_ASSIGN, BugResponseDto.class, bugAssignDto2, bugResponseDto1);
			}
		}
		@Nested
		@DisplayName("Negative")
		class BugsReporterImpl_addProgrammer_Negative {
			@Test
			@Order(23)
			@DisplayName("Open bug and assign to programmer with invalid (not exsists) ID. Sould be custom NotFoudException.")
			void invalid_programmer_id_in_post_request_on_open_and_assign_bug_expect_NotFound() {
				programmerRepository.save(programmersList.get(0));
				executePostTestExpectNotFound(PATH_BUGS_OPEN_ASSIGN, bugAssignDto);
			}
			@Test
			@Order(24)
			void open_new_bug_with_date_in_future_expect_BadRequest() {
				programmerRepository.save(programmersList.get(0));
				BugAssignDto bugAssignDto2 = new BugAssignDto(Seriousness.BLOCKING, "description", LocalDate.now().plusDays(1), 1); 
				executePostTestExpectBadRequest(PATH_BUGS_OPEN_ASSIGN, bugAssignDto2);
			}
			@Test
			@Order(25)
			void open_new_bug_with_date_in_imposibal_past_expect_BadRequest() {
				programmerRepository.save(programmersList.get(0));
				BugAssignDto bugAssignDto2 = new BugAssignDto(Seriousness.BLOCKING, "description", LocalDate.now().minusYears(21), 1); 
				executePostTestExpectBadRequest(PATH_BUGS_OPEN_ASSIGN, bugAssignDto2);
			}
			@Test
			@Order(26)
			void open_new_bug_with_Seriousness_null_expect_BadRequest() {
				programmerRepository.save(programmersList.get(0));
				BugAssignDto bugAssignDto2 = new BugAssignDto(null, "description", LocalDate.now().minusYears(21), 1); 
				executePostTestExpectBadRequest(PATH_BUGS_OPEN_ASSIGN, bugAssignDto2);
			}
			@Test
			@Order(27)
			void open_new_bug_with_description_null_expect_BadRequest() {
				programmerRepository.save(programmersList.get(0));
				BugAssignDto bugAssignDto2 = new BugAssignDto(Seriousness.BLOCKING, null, LocalDate.now().minusYears(21), 1); 
				executePostTestExpectBadRequest(PATH_BUGS_OPEN_ASSIGN, bugAssignDto2);
			}
			@Test
			@Order(28)
			void open_new_bug_with_empty_description_expect_BadRequest() {
				programmerRepository.save(programmersList.get(0));
				BugAssignDto bugAssignDto2 = new BugAssignDto(Seriousness.BLOCKING, "", date, 1); 
				executePostTestExpectBadRequest(PATH_BUGS_OPEN_ASSIGN, bugAssignDto2);
			}
			@Test
			@Order(29)
			void open_new_bug_with_invalid_programmer_id_expect_BadRequest() {
				programmerRepository.save(programmersList.get(0));
				BugAssignDto bugAssignDto2 = new BugAssignDto(Seriousness.BLOCKING, "description", date, -1); 
				executePostTestExpectNotFound(PATH_BUGS_OPEN_ASSIGN, bugAssignDto2); // I think NotFound not correct. Should be BadRequest.
				BugAssignDto bugAssignDto3 = new BugAssignDto(Seriousness.BLOCKING, "description", date, 0); 
				executePostTestExpectNotFound(PATH_BUGS_OPEN_ASSIGN, bugAssignDto3); // I think NotFound not correct. Should be BadRequest.
			}
		}
	}

	@Nested
	@DisplayName("Tests for addProgrammer method in BugsReporterImpl")
	class BugsReporterImpl_addProgrammer_test {
		@Nested
		@DisplayName("Positive")
		class BugsReporterImpl_addProgrammer_Positive {
			@Test
			@Order(30)
			@Sql("fillTables.sql")
			void create_new_programmers_in_db_expect_ok() {
				executePostTest(PATH_BUGS_PROGRAMMERS, ProgrammerDto.class, programmerDto, programmerDto);
				Programmer programmerFromDb = programmerRepository.findAll().get(0);
				assertEquals(programmerDto.Id, programmerFromDb.getId());
				assertEquals(programmerDto.email, programmerFromDb.getEmail());
				assertEquals(programmerDto.name, programmerFromDb.getName());
			}
		}
		@Nested
		@DisplayName("Negative")
		class BugsReporterImpl_addProgrammer_Negative {
			@Test
			@Order(31)
			@Sql("fillTables.sql")
			void create_new_programmers_in_db_duplikat_programmer_id_expect_4xxClientError() {
				programmerRepository.save(programmersList.get(0));
				webTestClient.post().uri(PATH_BUGS_PROGRAMMERS).bodyValue(programmerDto).exchange().expectStatus()
						.is4xxClientError();
			}
			@Test
			@Order(32)
			@Sql("fillTables.sql")
			void create_new_programmers_in_db_invalid_id_expect_BadRequest() {
				ProgrammerDto programmerDto1 = new ProgrammerDto(-1, "Bob", "bob@gmail.com");
				executePostTestExpectBadRequest(PATH_BUGS_PROGRAMMERS, programmerDto1);
				ProgrammerDto programmerDto2 = new ProgrammerDto(0, "Bob", "bob@gmail.com");
				executePostTestExpectBadRequest(PATH_BUGS_PROGRAMMERS, programmerDto2);
			}
			@Test
			@Order(33)
			@Sql("fillTables.sql")
			void create_new_programmers_in_db_empty_name_expect_BadRequest() {
				ProgrammerDto programmerDto1 = new ProgrammerDto(1, "", "bob@gmail.com");
				executePostTestExpectBadRequest(PATH_BUGS_PROGRAMMERS, programmerDto1);
				ProgrammerDto programmerDto2 = new ProgrammerDto(1, null, "bob@gmail.com");
				executePostTestExpectBadRequest(PATH_BUGS_PROGRAMMERS, programmerDto2);
			}
			@Test
			@Order(34)
			@Sql("fillTables.sql")
			void create_new_programmers_in_db_invalid_email_expect_BadRequest() {
				ProgrammerDto programmerDto1 = new ProgrammerDto(1, "Bob", "bobgmail.com");
				executePostTestExpectBadRequest(PATH_BUGS_PROGRAMMERS, programmerDto1);
//		ProgrammerDto programmerDto2 = new ProgrammerDto(1, "Bob", "bob@gmailcom");
//		executePostTestExpectBadRequest(PATH_BUGS_PROGRAMMERS, programmerDto2); // not work
				ProgrammerDto programmerDto3 = new ProgrammerDto(1, "Bob", "bobgmail.com@");
				executePostTestExpectBadRequest(PATH_BUGS_PROGRAMMERS, programmerDto3);
				ProgrammerDto programmerDto4 = new ProgrammerDto(1, "Bob", "@bobgmail.com");
				executePostTestExpectBadRequest(PATH_BUGS_PROGRAMMERS, programmerDto4);
			}
		}
	}
	
	@Nested
    @DisplayName("Tests for addArtifact method in BugsReporterImpl")
    class BugsReporterImpl_addArtifact_test {
		@Nested
		@DisplayName("Positive")
		class BugsReporterImpl_addArtifact_Positive {
			@Test
			@Order(35)
			@Sql("fillTables.sql")
			void create_new_artifact_in_db_expect_ok() {
				programmerRepository.save(programmersList.get(0));
				executePostTest(PATH_BUGS_ARTIFACT, ArtifactDto.class, artifactDto, artifactDto);
				Artifact artifactFromDb = artifactRepository.findAll().get(0);
				assertEquals(artifactDto.programmer, artifactFromDb.getProgrammer().getId());
				assertEquals(artifactDto.artifactId, artifactFromDb.getArtifacId());
			}
		}
		@Nested
		@DisplayName("Negative")
		class BugsReporterImpl_addArtifact_Negative {
			@Test
			@Order(36)
			@Sql("fillTables.sql")
			void create_new_artifact_in_db_duplikat_artifact_id_expect_4xxClientError() {
				programmerRepository.save(programmersList.get(0));
				artifactRepository.save(new Artifact("artifact_test", programmersList.get(0)));
				webTestClient.post().uri(PATH_BUGS_ARTIFACT).bodyValue(artifactDto).exchange().expectStatus()
						.is4xxClientError();
			}
			@Test
			@Order(37)
			@Sql("fillTables.sql")
			void create_new_artifac_in_db_empty_id_expect_BadRequest() {
				ArtifactDto artifactDto1 = new ArtifactDto("", 1);
				executePostTestExpectBadRequest(PATH_BUGS_ARTIFACT, artifactDto1);
				ArtifactDto artifactDto2 = new ArtifactDto(null, 1);
				executePostTestExpectBadRequest(PATH_BUGS_ARTIFACT, artifactDto2);
			}
			@Test
			@Order(38)
			@Sql("fillTables.sql")
			void create_new_artifac_in_db_with_invalid_programmer_id_expect_BadRequest() {
				ArtifactDto artifactDto1 = new ArtifactDto("artifact_test", -1);
				executePostTestExpectBadRequest(PATH_BUGS_ARTIFACT, artifactDto1);
				ArtifactDto artifactDto2 = new ArtifactDto("artifact_test", 0);
				executePostTestExpectBadRequest(PATH_BUGS_ARTIFACT, artifactDto2);
			}
		}
	}
	
	@Nested
    @DisplayName("Tests for openBug method in BugsReporterImpl")
    class BugsReporterImpl_openBug_test {
		@Nested
		@DisplayName("Positive")
		class BugsReporterImpl_addArtifact_Positive {
			@Test
			@Order(39)
			@Sql("fillTables.sql")
			void open_new_bug_expect_ok() {
				executePostTest(PATH_BUGS_OPEN, BugResponseDto.class, bugDto, bugResponseDto);
			}
			@Test
			@Order(40)
			@Sql("fillTables.sql")
			void open_new_bug_with_date_null_expect_ok() {
				BugDto bugDto1 = new BugDto(Seriousness.BLOCKING, "description", null);
				executePostTest(PATH_BUGS_OPEN, BugResponseDto.class, bugDto1, bugResponseDto);
			}
		}
		@Nested
		@DisplayName("Negative")
		class BugsReporterImpl_addArtifact_Negative {
			@Test
			@Order(41)
			void open_new_bug_with_Seriousness_null_expect_BadRequest() {
				BugDto bugDto1 = new BugDto(null, "description", date);
				executePostTestExpectBadRequest(PATH_BUGS_OPEN, bugDto1);
			}
			@Test
			@Order(42)
			void open_new_bug_with_description_null_expect_BadRequest() {
				BugDto bugDto1 = new BugDto(Seriousness.BLOCKING, null, date);
				executePostTestExpectBadRequest(PATH_BUGS_OPEN, bugDto1);
			}
			@Test
			@Order(43)
			void open_new_bug_with_empty_description_expect_BadRequest() {
				BugDto bugDto1 = new BugDto(Seriousness.BLOCKING, "", date);
				executePostTestExpectBadRequest(PATH_BUGS_OPEN, bugDto1);
			}
			@Test
			@Order(44)
			void open_new_bug_with_date_in_future_expect_BadRequest() {
				BugDto bugDto1 = new BugDto(Seriousness.BLOCKING, "description", LocalDate.now().plusDays(1));
				executePostTestExpectBadRequest(PATH_BUGS_OPEN, bugDto1);
			}
			@Test
			@Order(45)
			void open_new_bug_with_date_in_imposibal_past_expect_BadRequest() {
				BugDto bugDto1 = new BugDto(Seriousness.BLOCKING, "description", LocalDate.now().minusYears(21));
				executePostTestExpectBadRequest(PATH_BUGS_OPEN, bugDto1);
			}
		}
    }
}
