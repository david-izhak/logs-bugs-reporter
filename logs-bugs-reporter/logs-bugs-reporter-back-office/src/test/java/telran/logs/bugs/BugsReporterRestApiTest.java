package telran.logs.bugs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Min;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import lombok.extern.slf4j.Slf4j;
import telran.logs.bugs.dto.AssignBugData;
import telran.logs.bugs.dto.BugAssignDto;
import telran.logs.bugs.dto.BugDto;
import telran.logs.bugs.dto.BugResponseDto;
import telran.logs.bugs.dto.BugStatus;
import telran.logs.bugs.dto.LogDto;
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
@Slf4j
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
	
//	private <T> void executePostTest(String path, Class<T> clazz, T expected) {
//		 webTestClient.post()
//				.uri(path)
//				.bodyValue(expected)
//				.exchange()
//				.expectStatus().isOk()
//				.expectBody(clazz)
//				.isEqualTo(expected);
//	}
	
	@Test
	void createProgrammersTest() {
		ProgrammerDto programmerDto = new ProgrammerDto(1, "Bob", "bob@gmail.com");
		webTestClient.post()
			.uri(pathBugsProgrammers)
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(programmerDto)
			.exchange()
			.expectStatus().isOk()
			.expectBody(ProgrammerDto.class)
			.isEqualTo(programmerDto);
//		executeTest(pathBugsProgrammers, programmerDto, ProgrammerDto.class, programmerDto);
	}
	
	@Test
	void openBugTest() {
		LocalDate date = LocalDate.now();
		BugDto bugDto = new BugDto(Seriousness.BLOCKING, "description", date);
		BugResponseDto bugResponseDto = new BugResponseDto (1, Seriousness.BLOCKING, "description", date, 0, null, BugStatus.OPENND, OpeningMethod.MANUAL);
		webTestClient.post()
			.uri(pathBugsOpen)
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(bugDto)
			.exchange()
			.expectStatus().isOk()
			.expectBody(BugResponseDto.class)
			.isEqualTo(bugResponseDto);
	}
	
	@Test
	void openBugAssignTest() {
		LocalDate date = LocalDate.now();
		BugAssignDto bugAssignDto = new BugAssignDto(Seriousness.BLOCKING, "description", date, 1);
		BugResponseDto bugResponseDto = new BugResponseDto (1, Seriousness.BLOCKING, "description", date, 1, null, BugStatus.ASSIGNED, OpeningMethod.MANUAL);
		webTestClient.post()
		.uri(pathBugsOpenAssign)
		.contentType(MediaType.APPLICATION_JSON)
		.bodyValue(bugAssignDto)
		.exchange()
		.expectStatus().isOk()
		.expectBody(BugResponseDto.class)
		.isEqualTo(bugResponseDto);
	}
	
	@Test
	void assignBugTest() { // not work
		LocalDate date = LocalDate.now();
		AssignBugData assignBugData = new AssignBugData(1, 1, "assign_description");
		Programmer programmer = new Programmer(1, "Bob", "bob@gmail.com");
		Bug bug = new Bug("bug_description", date , null, BugStatus.OPENND, Seriousness.BLOCKING, OpeningMethod.MANUAL, null);
		bugRepository.save(bug);
		programmerRepository.save(programmer);
		Bug bugExp = new Bug("bug_description" + "\nAssignment Description: " + "assign_description", date , null, BugStatus.ASSIGNED, Seriousness.BLOCKING, OpeningMethod.MANUAL, programmer);
		Bug bugFromRepo = bugRepository.findAll().get(0);
		webTestClient.put()
		.uri(pathBugsAssign)
		.contentType(MediaType.APPLICATION_JSON)
		.bodyValue(assignBugData)
		.exchange()
		.expectStatus().isOk();
		assertEquals(bugExp, bugFromRepo);
	}
	
	@Test
	void getBugsProgrammerTest() {
		LocalDate date = LocalDate.now();
		Programmer programmer = new Programmer(1, "Bob", "bob@gmail.com");
		programmerRepository.save(programmer);
		Bug bug = new Bug("bug_description", date , null, BugStatus.ASSIGNED, Seriousness.BLOCKING, OpeningMethod.MANUAL, programmer);
		bugRepository.save(bug);
		List<BugResponseDto> listExp = new ArrayList<>();
		listExp.add(new BugResponseDto (1, Seriousness.BLOCKING, "description", date, 1, null, BugStatus.ASSIGNED, OpeningMethod.MANUAL));
		BugResponseDto bugResponseDto = new BugResponseDto (1, Seriousness.BLOCKING, "description", date, 1, null, BugStatus.ASSIGNED, OpeningMethod.MANUAL);
		webTestClient.get()
		.uri(pathBugsProgrammersGet).exchange()
		.expectStatus().isOk()
		.expectBodyList(BugResponseDto.class)
		.isEqualTo(listExp);
	}
}
