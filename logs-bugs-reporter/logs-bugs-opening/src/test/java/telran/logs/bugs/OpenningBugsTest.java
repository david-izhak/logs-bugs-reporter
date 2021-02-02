package telran.logs.bugs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import telran.logs.bugs.jpa.entities.Artifact;
import telran.logs.bugs.jpa.entities.Programmer;

@SpringBootTest
@AutoConfigureTestDatabase
public class OpenningBugsTest {
	
	@Autowired
	ProgrammersRepo programmers;
	
	@Autowired
	ArtifactsRepo artifacts;
	
	@Autowired
	BugsRepo bugs;
	
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
		System.out.println(artifactExp.toString());
		System.out.println(artifactList.get(0).toString());
		assertEquals(artifactExp, artifactList.get(0));
	}
}
