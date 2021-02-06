package telran.logs.bugs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.jdbc.Sql;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.jpa.entities.Artifact;
import telran.logs.bugs.jpa.entities.Bug;
import telran.logs.bugs.jpa.entities.BugStatus;
import telran.logs.bugs.jpa.entities.OpeningMethod;
import telran.logs.bugs.jpa.entities.Programmer;
import telran.logs.bugs.jpa.entities.Seriousness;
import telran.logs.bugs.services.LogDtoToBugConverterInterface;

@SpringBootTest
@AutoConfigureTestDatabase
@Import(TestChannelBinderConfiguration.class)
class OpenningBugsTest {
	
	final String INIT_SQL = "fillTables.sql";
	
	@Autowired
	ProgrammersRepo programmers;
	
	@Autowired
	ArtifactsRepo artifacts;
	
	@Autowired
	BugsRepo bugs;
	
	@Autowired
	LogDtoToBugConverterInterface bugsOpeningService;
	
	@Autowired
	InputDestination input;
	
	Programmer programmerMoshe = new Programmer(1, "Moshe", "moshe@mail.com");
	
	LogDto logDto_AUTHENTICATION_EXCEPTION = new LogDto(new Date(), LogType.AUTHENTICATION_EXCEPTION, "bug1", 20, "resultTest");
	Bug bug_AUTHENTICATION_EXCEPTION = Bug.builder()
			.description("AUTHENTICATION_EXCEPTION resultTest")
			.dateOpen(LocalDate.now())
			.dateClose(null)
			.status(BugStatus.ASSIGNED)
			.seriousness(Seriousness.BLOCKING)
			.openningMethod(OpeningMethod.AUTOMATIC)
			.programmer(programmerMoshe)
			.build();
	
	LogDto logDto_BAD_REQUEST_EXCEPTION = new LogDto(new Date(), LogType.BAD_REQUEST_EXCEPTION, "bug1", 20, "resultTest");
	Bug bug_BAD_REQUEST_EXCEPTION = Bug.builder()
			.description("BAD_REQUEST_EXCEPTION resultTest")
			.dateOpen(LocalDate.now())
			.dateClose(null)
			.status(BugStatus.ASSIGNED)
			.seriousness(Seriousness.MINOR)
			.openningMethod(OpeningMethod.AUTOMATIC)
			.programmer(programmerMoshe)
			.build();
	
	LogDto logDto_NOT_FOUND_EXCEPTION = new LogDto(new Date(), LogType.NOT_FOUND_EXCEPTION, "bug1", 20, "resultTest");
	Bug bug_NOT_FOUND_EXCEPTION = Bug.builder()
			.description("NOT_FOUND_EXCEPTION resultTest")
			.dateOpen(LocalDate.now())
			.dateClose(null)
			.status(BugStatus.ASSIGNED)
			.seriousness(Seriousness.MINOR)
			.openningMethod(OpeningMethod.AUTOMATIC)
			.programmer(programmerMoshe)
			.build();
	
	LogDto logDto_DUPLICATED_KEY_EXCEPTION = new LogDto(new Date(), LogType.DUPLICATED_KEY_EXCEPTION, "bug1", 20, "resultTest");
	Bug bug_DUPLICATED_KEY_EXCEPTION = Bug.builder()
			.description("DUPLICATED_KEY_EXCEPTION resultTest")
			.dateOpen(LocalDate.now())
			.dateClose(null)
			.status(BugStatus.ASSIGNED)
			.seriousness(Seriousness.MINOR)
			.openningMethod(OpeningMethod.AUTOMATIC)
			.programmer(programmerMoshe)
			.build();
	
	LogDto logDto_SERVER_EXCEPTION = new LogDto(new Date(), LogType.SERVER_EXCEPTION, "bug1", 20, "resultTest");
	Bug bug_SERVER_EXCEPTION = Bug.builder()
			.description("SERVER_EXCEPTION resultTest")
			.dateOpen(LocalDate.now())
			.dateClose(null)
			.status(BugStatus.ASSIGNED)
			.seriousness(Seriousness.CRITICAL)
			.openningMethod(OpeningMethod.AUTOMATIC)
			.programmer(programmerMoshe)
			.build();
	
	LogDto logDto_AUTHORIZATION_EXCEPTION = new LogDto(new Date(), LogType.AUTHORIZATION_EXCEPTION, "bug1", 20, "resultTest");
	Bug bug_AUTHORIZATION_EXCEPTION = Bug.builder()
			.description("AUTHORIZATION_EXCEPTION resultTest")
			.dateOpen(LocalDate.now())
			.dateClose(null)
			.status(BugStatus.ASSIGNED)
			.seriousness(Seriousness.CRITICAL)
			.openningMethod(OpeningMethod.AUTOMATIC)
			.programmer(programmerMoshe)
			.build();
	
	LogDto logDtoNoProgrammer = new LogDto(new Date(), LogType.AUTHORIZATION_EXCEPTION, "bug2", 20, "resultTest");
	Bug bugNoProgrammer = Bug.builder()
			.description("AUTHORIZATION_EXCEPTION resultTest")
			.dateOpen(LocalDate.now())
			.dateClose(null)
			.status(BugStatus.OPENND)
			.seriousness(Seriousness.CRITICAL)
			.openningMethod(OpeningMethod.AUTOMATIC)
			.programmer(null)
			.build();
	
	LogDto logDto_NO_EXCEPTION = new LogDto(new Date(), LogType.NO_EXCEPTION, "bug1", 20, "resultTest");
	Bug bug_NO_EXCEPTION = Bug.builder()
			.description("AUTHORIZATION_EXCEPTION resultTest")
			.dateOpen(LocalDate.now())
			.dateClose(null)
			.status(BugStatus.ASSIGNED)
			.seriousness(null)
			.openningMethod(OpeningMethod.AUTOMATIC)
			.programmer(programmerMoshe)
			.build();

	
	@Test
	@Sql(INIT_SQL)
	void primitiveTest() {
		programmers.save(new Programmer(123, "Sara", "sara@mail.com"));
		assertEquals(2, programmers.count());
	}
	
	@Test
	@Sql(INIT_SQL)
	void restoreProgrammerTest() {
		List<Programmer> programmersList = programmers.findAll();
		assertEquals(1, programmersList.size());
		assertEquals(programmerMoshe, programmersList.get(0));
	}
	
	@Test
	@Sql(INIT_SQL)
	void restoreArtifactTest() {
		Artifact artifactExp = new Artifact("bug1", programmerMoshe);
		List<Artifact> artifactList = artifacts.findAll();
		assertEquals(1, artifactList.size());
		assertEquals(artifactExp, artifactList.get(0));
	}
	
	@Test
	@Sql(INIT_SQL)
	void createSaveRestoreBugTest1() {
		executTest(logDto_AUTHENTICATION_EXCEPTION, bug_AUTHENTICATION_EXCEPTION);
	}

	@Test
	@Sql(INIT_SQL)
	void createSaveRestoreBugTest2() {
		executTest(logDto_BAD_REQUEST_EXCEPTION, bug_BAD_REQUEST_EXCEPTION);
	}

	@Test
	@Sql(INIT_SQL)
	void createSaveRestoreBugTest3() {
		executTest(logDto_NOT_FOUND_EXCEPTION, bug_NOT_FOUND_EXCEPTION);
	}

	@Test
	@Sql(INIT_SQL)
	void createSaveRestoreBugTest4() {
		executTest(logDto_DUPLICATED_KEY_EXCEPTION, bug_DUPLICATED_KEY_EXCEPTION);
	}

	@Test
	@Sql(INIT_SQL)
	void createSaveRestoreBugTest5() {
		executTest(logDto_SERVER_EXCEPTION, bug_SERVER_EXCEPTION);
	}

	@Test
	@Sql(INIT_SQL)
	void createSaveRestoreBugTest6() {
		executTest(logDto_AUTHORIZATION_EXCEPTION, bug_AUTHORIZATION_EXCEPTION);
	}
	
	@Test
	@Sql(INIT_SQL)
	void createSaveRestoreBugTest7() {
		executTest(logDtoNoProgrammer, bugNoProgrammer);
	}

	private void executTest(LogDto inputLogDto, Bug bugExpected) {
		input.send(new GenericMessage<LogDto>(inputLogDto));
		List<Bug> bugsList = bugs.findAll();
		assertEquals(1, bugsList.size());
		assertEquals(bugExpected, bugsList.get(0));
	}

	@Test
	@Sql(INIT_SQL)
	void createSaveRestoreBugTest8() {
		input.send(new GenericMessage<LogDto>(logDto_NO_EXCEPTION));
		List<Bug> bugsList = bugs.findAll();
		assertEquals(0, bugsList.size());
	}
}
