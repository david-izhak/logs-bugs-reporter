package telran.logs.bugs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
	ArtifactRepository artifactRepository;

	@Autowired
	BugRepository bugRepository;

	@Autowired
	ProgrammerRepository programmerRepository;

	@Value("${path-bugs-programmers}")
	String pathBugsProgrammers;

	@Value("${path-bugs-open}")
	String pathBugsOpen;

	@Value("${path-bugs-open-assign}")
	String pathBugsOpenAssign;

	@Value("${path-bugs-assign}")
	String pathBugsAssign;

	@Value("${path-bugs-programmers-get}")
	String pathBugsProgrammersGet;
	
	@BeforeEach
	void cleanRepos() {
		artifactRepository.deleteAll();
		bugRepository.deleteAll();
		programmerRepository.deleteAll();
	}

//	Common test for POST
	private <T, P> void executePostTest(String path, Class<P> clazz, T body, P expected) {
		webTestClient.post().uri(path).bodyValue(body).exchange().expectStatus().isOk().expectBody(clazz)
				.isEqualTo(expected);
	}

//	Common test for GET(1)
	private void executeGetTest(String path, AssignBugData assignBugData) {
		webTestClient.put().uri(path).contentType(MediaType.APPLICATION_JSON).bodyValue(assignBugData).exchange()
				.expectStatus().isOk();
	}

//	Common test for GET(2)
	private <T> void executeGetTest(String path, Class<T> clazz, List<T> list) {
		webTestClient.get().uri(path).exchange().expectStatus().isOk().expectBodyList(clazz).isEqualTo(list);
	}

//	Order of test matters because the table "BUG" in the DB has autoincrement in column "ID". 
//	Despite the fact that data from tables is deleted before each test, value of previous ID is saved.

	@Test
	@Order(1)
	void openBugAssignTest() {
//		Prepare data and repos
		LocalDate date = LocalDate.now();
		BugAssignDto bugAssignDto = new BugAssignDto(Seriousness.BLOCKING, "description", date, 1);
		Programmer programmer = new Programmer(1, "Bob", "bob@gmail.com");
		programmerRepository.save(programmer);
		BugResponseDto bugResponseDto = new BugResponseDto(1, Seriousness.BLOCKING, "description", date, 1, null,
				BugStatus.ASSIGNED, OpeningMethod.MANUAL);
//		Execute test
		executePostTest(pathBugsOpenAssign, BugResponseDto.class, bugAssignDto, bugResponseDto);
	}

	@Test
	@Order(2)
	void assignBugTest() {
//		Prepare data and repos
		LocalDate date = LocalDate.now();
		AssignBugData assignBugData = new AssignBugData(2, 1, "assign_description"); // First field equals 2 (ID of the
																						// bug) because DB incremented
																						// ID of bug in previous test
																						// #1.
		Programmer programmer = new Programmer(1, "Bob", "bob@gmail.com");
		programmerRepository.save(programmer);
		Bug bug = new Bug("bug_description", date, null, BugStatus.OPENND, Seriousness.BLOCKING, OpeningMethod.MANUAL,
				null);
		bugRepository.save(bug);
		Bug bugExp = new Bug("bug_description" + "\nAssignment Description: " + "assign_description", date, null,
				BugStatus.ASSIGNED, Seriousness.BLOCKING, OpeningMethod.MANUAL, programmer);
//		Execute test
		executeGetTest(pathBugsAssign, assignBugData);
		assertEquals(bugExp, bugRepository.findAll().get(0));
	}

	@Test
	@Order(3)
	void getBugsProgrammerTest() {
//		Prepare data and repos
		LocalDate date = LocalDate.now();
		Programmer programmer = new Programmer(1, "Bob", "bob@gmail.com");
		programmerRepository.save(programmer);
		Bug bug = new Bug("bug_description", date, null, BugStatus.ASSIGNED, Seriousness.BLOCKING, OpeningMethod.MANUAL,
				programmer);
		bugRepository.save(bug);
		List<BugResponseDto> listExp = new ArrayList<>();
		listExp.add(new BugResponseDto(3, Seriousness.BLOCKING, "bug_description", date, 1, null, BugStatus.ASSIGNED,
				OpeningMethod.MANUAL)); // First field equals 3 (ID of the bug) because DB incremented ID of bug in
										// previous two tests #1 and #2.
//		Execute test
		executeGetTest(pathBugsProgrammersGet, BugResponseDto.class, listExp);
	}

	@Test
	@Order(4)
	void openBugTest() {
//		Prepare data and repos
		LocalDate date = LocalDate.now();
		BugDto bugDto = new BugDto(Seriousness.BLOCKING, "description", date);
		BugResponseDto bugResponseDto = new BugResponseDto(4, Seriousness.BLOCKING, "description", date, 0, null,
				BugStatus.OPENND, OpeningMethod.MANUAL); // First field equals 4 (ID of the bug) because DB incremented
															// ID of bug in previous three tests #1, #2 and #3.
//		Execute test
		executePostTest(pathBugsOpen, BugResponseDto.class, bugDto, bugResponseDto);
	}

	@Test
	@Order(5)
	void createProgrammersTest() {
//		Prepare data and repos
		ProgrammerDto programmerDto = new ProgrammerDto(1, "Bob", "bob@gmail.com");
//		Execute test
		executePostTest(pathBugsProgrammers, ProgrammerDto.class, programmerDto, programmerDto);
	}

	@Test
	@Order(6)
	@DisplayName("Test getEmailBugsCount method of controller")
	void emailCounts() {
//		Prepare data and repos
		LocalDate date = LocalDate.now();
		List<Programmer> programmersList = Arrays.asList(
				new Programmer(1, "Bob", "bob@gmail.com"), 
				new Programmer(2, "John", "jhon@gmail.com"), 
				new Programmer(3, "Anna", "anna@gmail.com"));
		programmerRepository.saveAll(programmersList);
		List<Bug> bugsList = Arrays.asList(
				new Bug("bug_description", date, null, BugStatus.ASSIGNED, Seriousness.BLOCKING, OpeningMethod.MANUAL, programmersList.get(0)),
				new Bug("bug_description", date, null, BugStatus.ASSIGNED, Seriousness.BLOCKING, OpeningMethod.MANUAL, programmersList.get(0)),
				new Bug("bug_description", date, null, BugStatus.ASSIGNED, Seriousness.BLOCKING, OpeningMethod.MANUAL, programmersList.get(0)),
				new Bug("bug_description", date, null, BugStatus.ASSIGNED, Seriousness.BLOCKING, OpeningMethod.MANUAL, programmersList.get(1)),
				new Bug("bug_description", date, null, BugStatus.ASSIGNED, Seriousness.BLOCKING, OpeningMethod.MANUAL, programmersList.get(1)));
		bugRepository.saveAll(bugsList);
		List<EmailBugCountTest> expectedEmailCounts = Arrays.asList(new EmailBugCountTest("bob@gmail.com", 3),
				new EmailBugCountTest("jhon@gmail.com", 2), new EmailBugCountTest("anna@gmail.com", 0));
//		Execute test
		executeGetTest(BUGS_PROGRAMMERS_COUNT, EmailBugCountTest.class, expectedEmailCounts);
	}

	@Test
	@Order(7)
	void createArtifactTest() {
//		Prepare data and repos
		Programmer programmer = new Programmer(1, "Bob", "bob@gmail.com");
		programmerRepository.save(programmer);
		ArtifactDto artifactDto = new ArtifactDto("artifact_test", 1);
//		Execute test
		executePostTest(PATH_BUGS_ARTIFACT, ArtifactDto.class, artifactDto, artifactDto);
	}

	@Test
	@Order(8)
	void closeBugTest() {
//		Prepare data and repos
		LocalDate date = LocalDate.now();
		CloseBugData closeBugData = new CloseBugData(10, date, "closeBugData_description"); // First field equals 10 (ID
																							// of the
																							// bug) because DB
																							// incremented
																							// ID of bug in previous
																							// tests.
		Bug bug = new Bug("bug_description", date, null, BugStatus.OPENND, Seriousness.BLOCKING, OpeningMethod.MANUAL,
				null);
		bugRepository.save(bug);
		Bug bugExp = new Bug(10, "bug_description" + "\nClose Description: " + "closeBugData_description", date, date,
				BugStatus.CLOSED, Seriousness.BLOCKING, OpeningMethod.MANUAL, null);
//		Execute test
		webTestClient.put().uri(PATH_BUGS_CLOSE_DATA).contentType(MediaType.APPLICATION_JSON).bodyValue(closeBugData)
				.exchange().expectStatus().isOk();
		log.debug("====> bug {}", bugRepository.findAll().get(0));
		assertEquals(bugExp, bugRepository.findAll().get(0));
	}

	@Test
	@Order(9)
	@DisplayName("get Programmers with Most Bugs")
	void getProgrammersMostBugsTest() {
//		Prepare data and repos
		LocalDate date = LocalDate.now();
		List<Programmer> programmersList = Arrays.asList(
				new Programmer(1, "Bob", "bob@gmail.com"), 
				new Programmer(2, "John", "jhon@gmail.com"), 
				new Programmer(3, "Anna", "anna@gmail.com"), 
				new Programmer(4, "Tedd", "ted@gmail.com"));
		programmerRepository.saveAll(programmersList);
		List<Bug> bugsList = Arrays.asList(
				new Bug("bug_description", date, null, BugStatus.ASSIGNED, Seriousness.BLOCKING, OpeningMethod.MANUAL, programmersList.get(0)),
				new Bug("bug_description", date, null, BugStatus.ASSIGNED, Seriousness.BLOCKING, OpeningMethod.MANUAL, programmersList.get(0)),
				new Bug("bug_description", date, null, BugStatus.ASSIGNED, Seriousness.BLOCKING, OpeningMethod.MANUAL, programmersList.get(0)),
				new Bug("bug_description", date, null, BugStatus.ASSIGNED, Seriousness.BLOCKING, OpeningMethod.MANUAL, programmersList.get(1)),
				new Bug("bug_description", date, null, BugStatus.ASSIGNED, Seriousness.BLOCKING, OpeningMethod.MANUAL, programmersList.get(1)),
				new Bug("bug_description", date, null, BugStatus.ASSIGNED, Seriousness.BLOCKING, OpeningMethod.MANUAL, programmersList.get(2)));
		bugRepository.saveAll(bugsList);
		String[] expectedArray = { "Bob", "John" };
//		Execute test
		webTestClient.get().uri(BUGS_MOST_N_PROGRAMMERS + "?n_programmers=2").exchange().expectStatus().isOk()
				.expectBody(String[].class).isEqualTo(expectedArray);
	}

	@Test
	@Order(10)
	@DisplayName("Get programmers with least quantity of bugs")
	void getProgrammersWithLeastQuantityOfBugsTest() {
//		Prepare data and repos
		LocalDate date = LocalDate.now();
		List<Programmer> programmersList = Arrays.asList(
				new Programmer(1, "Bob", "bob@gmail.com"), 
				new Programmer(2, "John", "jhon@gmail.com"), 
				new Programmer(3, "Anna", "anna@gmail.com"), 
				new Programmer(4, "Tedd", "ted@gmail.com"));
		programmerRepository.saveAll(programmersList);
		List<Bug> bugsList = Arrays.asList(
				new Bug("bug_description", date, null, BugStatus.ASSIGNED, Seriousness.BLOCKING, OpeningMethod.MANUAL, programmersList.get(0)),
				new Bug("bug_description", date, null, BugStatus.ASSIGNED, Seriousness.BLOCKING, OpeningMethod.MANUAL, programmersList.get(0)),
				new Bug("bug_description", date, null, BugStatus.ASSIGNED, Seriousness.BLOCKING, OpeningMethod.MANUAL, programmersList.get(0)),
				new Bug("bug_description", date, null, BugStatus.ASSIGNED, Seriousness.BLOCKING, OpeningMethod.MANUAL, programmersList.get(1)),
				new Bug("bug_description", date, null, BugStatus.ASSIGNED, Seriousness.BLOCKING, OpeningMethod.MANUAL, programmersList.get(1)),
				new Bug("bug_description", date, null, BugStatus.ASSIGNED, Seriousness.BLOCKING, OpeningMethod.MANUAL, programmersList.get(2)));
		bugRepository.saveAll(bugsList);
		String[] expectedArray = { "Tedd", "Anna" };
//		Execute test
		webTestClient.get().uri(BUGS_LEAST_N_PROGRAMMERS + "?n_programmers=2").exchange().expectStatus().isOk()
				.expectBody(String[].class).isEqualTo(expectedArray);
	}
	
	@Test
	@Order(11)
	@DisplayName("Distribution of bugs according seriousness")
	void seriousnessDistribution() {
//		Prepare data and repos
		LocalDate date = LocalDate.now();
		List<Bug> bugsList = Arrays.asList(
				new Bug("bug_description", date, null, BugStatus.OPENND, Seriousness.BLOCKING, OpeningMethod.MANUAL, null),
				new Bug("bug_description", date, null, BugStatus.OPENND, Seriousness.BLOCKING, OpeningMethod.MANUAL, null),
				new Bug("bug_description", date, null, BugStatus.OPENND, Seriousness.BLOCKING, OpeningMethod.MANUAL, null),
				new Bug("bug_description", date, null, BugStatus.OPENND, Seriousness.COSMETIC, OpeningMethod.MANUAL, null),
				new Bug("bug_description", date, null, BugStatus.OPENND, Seriousness.COSMETIC, OpeningMethod.MANUAL, null),
				new Bug("bug_description", date, null, BugStatus.OPENND, Seriousness.MINOR, OpeningMethod.MANUAL, null),
				new Bug("bug_description", date, null, BugStatus.OPENND, Seriousness.CRITICAL, OpeningMethod.MANUAL, null));
		bugRepository.saveAll(bugsList);
		SeriousnessBugCount[] seriousnessBugCount = {
				new SeriousnessBugCount(Seriousness.BLOCKING, 3),
				new SeriousnessBugCount(Seriousness.COSMETIC, 2),
				new SeriousnessBugCount(Seriousness.CRITICAL, 1),
				new SeriousnessBugCount(Seriousness.MINOR, 1),
				};
//		Execute test
		webTestClient.get()
		.uri(BUGS_SERIOUSNESS_COUNT).exchange()
		.expectStatus().isOk()
		.expectBody(SeriousnessBugCount[].class)
		.isEqualTo(seriousnessBugCount);
	}
	
	@Test
	@Order(11)
	@DisplayName("Order of Seriousness types according bugs count")
	void seriousnessTypesOrderAccordingBugsCount() {
//		Prepare data and repos
		LocalDate date = LocalDate.now();
		List<Bug> bugsList = Arrays.asList(
				new Bug("bug_description", date, null, BugStatus.OPENND, Seriousness.BLOCKING, OpeningMethod.MANUAL, null),
				new Bug("bug_description", date, null, BugStatus.OPENND, Seriousness.BLOCKING, OpeningMethod.MANUAL, null),
				new Bug("bug_description", date, null, BugStatus.OPENND, Seriousness.BLOCKING, OpeningMethod.MANUAL, null),
				new Bug("bug_description", date, null, BugStatus.OPENND, Seriousness.COSMETIC, OpeningMethod.MANUAL, null),
				new Bug("bug_description", date, null, BugStatus.OPENND, Seriousness.COSMETIC, OpeningMethod.MANUAL, null),
				new Bug("bug_description", date, null, BugStatus.OPENND, Seriousness.MINOR, OpeningMethod.MANUAL, null),
				new Bug("bug_description", date, null, BugStatus.OPENND, Seriousness.CRITICAL, OpeningMethod.MANUAL, null));
		bugRepository.saveAll(bugsList);
		List<Seriousness> seriousnessList = Arrays.asList(
				Seriousness.BLOCKING,
				Seriousness.COSMETIC
		);
//		Execute test
		webTestClient.get()
		.uri(BUGS_SERIOUSNESS_MOST + "?n_seriousness=2").exchange()
		.expectStatus().isOk()
		.expectBodyList(Seriousness.class)
		.isEqualTo(seriousnessList);
	}
	
	@Test
	void invalidNProgrammersInRestGetProgrammersLeastBugs() {
		String uriStr = BUGS_LEAST_N_PROGRAMMERS + "?n_programmers=-1";
		invalidGetRequest(uriStr);
	}

	private void invalidGetRequest(String uriStr) {
		webTestClient.get()
		.uri(uriStr)
		.exchange()
		.expectStatus().isBadRequest();
	}
}
