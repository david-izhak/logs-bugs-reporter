package telran.logs.bugs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import telran.logs.bugs.dto.AssignBugData;
import telran.logs.bugs.dto.BugAssignDto;
import telran.logs.bugs.dto.BugDto;
import telran.logs.bugs.dto.BugResponseDto;
import telran.logs.bugs.dto.BugStatus;
import telran.logs.bugs.dto.OpeningMethod;
import telran.logs.bugs.dto.ProgrammerDto;
import telran.logs.bugs.dto.Seriousness;
import telran.logs.bugs.jpa.entities.Bug;
import telran.logs.bugs.jpa.entities.Programmer;
import telran.logs.bugs.jpa.repo.ArtifactRepository;
import telran.logs.bugs.jpa.repo.BugRepository;
import telran.logs.bugs.jpa.repo.ProgrammerRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestMethodOrder(OrderAnnotation.class)
public class BugsReporterRestApiTest {

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
		webTestClient.post()
		.uri(path)
		.bodyValue(body)
		.exchange()
		.expectStatus().isOk()
		.expectBody(clazz)
		.isEqualTo(expected);
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
		webTestClient.put()
		.uri(pathBugsAssign)
		.contentType(MediaType.APPLICATION_JSON)
		.bodyValue(assignBugData)
		.exchange()
		.expectStatus().isOk();
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
		webTestClient.get()
		.uri(pathBugsProgrammersGet)
		.exchange()
		.expectStatus().isOk()
		.expectBodyList(BugResponseDto.class)
		.isEqualTo(listExp);
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
}
