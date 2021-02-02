package telran.logs.bugs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.jpa.entities.Artifact;
import telran.logs.bugs.jpa.entities.Bug;
import telran.logs.bugs.jpa.entities.BugStatus;
import telran.logs.bugs.jpa.entities.OpenningMethod;
import telran.logs.bugs.jpa.entities.Programmer;
import telran.logs.bugs.jpa.entities.Seriousness;

@SpringBootTest
@AutoConfigureTestDatabase
public class OpenningBugsTest {
	
	@Autowired
	ProgrammersRepo programmers;
	
	@Autowired
	ArtifactsRepo artifacts;
	
	@Autowired
	BugsRepo bugs;
	
	@Autowired
	BugsOpenningAppl bugsOpenningAppl;
	
	@Test
	void primitiveTest() {
		programmers.save(new Programmer(123, "Moshe"));
	}
	
	@Test
	@Sql("fillTables.sql")
	void restorProgrammerTest() {
		Programmer programmerExp = new Programmer(1, "Moshe");
		List<Programmer> programmersList = programmers.findAll();
		assertEquals(1, programmersList.size());
		assertEquals(programmerExp, programmersList.get(0));
	}
	
	@Test
	@Sql("fillTables.sql")
	void restorArtifactTest() {
		Programmer programmer = new Programmer(1, "Moshe");
		Artifact artifactExp = new Artifact("bug1", programmer);
		List<Artifact> artifactList = artifacts.findAll();
		assertEquals(1, artifactList.size());
		assertEquals(artifactExp, artifactList.get(0));
	}
	
	@Test
	@Sql("fillTables.sql")
	void createSaveRestorBugTest() {
		LogDto logDto = new LogDto(new Date(), LogType.AUTHENTICATION_EXCEPTION, "bug1", 20, "resultMock");
		bugsOpenningAppl.takeLogDtoAndOpenBug(logDto);
		List<Bug> bugsList = bugs.findAll();
		assertEquals(1, bugsList.size());
		assertNotNull(bugsList.get(0).getDateOpen());
		assertEquals(null, bugsList.get(0).getDateClose());
		assertEquals(OpenningMethod.AUTOMATIC, bugsList.get(0).getOpenningMethod());
		assertEquals(BugStatus.ASSIGNED, bugsList.get(0).getStatus());
		assertEquals(Seriousness.BLOCKING, bugsList.get(0).getSeriousness());
		
	}
}
