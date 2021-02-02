package telran.logs.bugs;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import telran.logs.bugs.jpa.entities.Artifact;
import telran.logs.bugs.jpa.entities.Bug;
import telran.logs.bugs.jpa.entities.BugStatus;
import telran.logs.bugs.jpa.entities.OpenningMethod;
import telran.logs.bugs.jpa.entities.Programmer;
import telran.logs.bugs.jpa.entities.Seriousness;

@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@ContextConfiguration(classes = { ProgrammersRepo.class, ArtifactsRepo.class, BugsRepo.class })
public class EntitiesTest {

	@Autowired
	ProgrammersRepo programmers;

	@Autowired
	ArtifactsRepo artifacts;

	@Autowired
	BugsRepo bugs;

	@Test
	void bugCreationTest() {
		Programmer programmer = new Programmer(123, "Moshe");
		programmers.save(programmer);
		Artifact artifact = new Artifact("authentication", programmer);
		artifacts.save(artifact);
		Bug bug = new Bug("description", LocalDate.now(), LocalDate.now(), BugStatus.ASSIGNED, Seriousness.MINOR, OpenningMethod.AUTOMATIC, programmer);
		bugs.save(bug);
		
		List<Bug> bugsRes = bugs.findAll();
		
		assertEquals(1, bugsRes.size());
		assertEquals(bug, bugsRes.get(0));
	}

}
