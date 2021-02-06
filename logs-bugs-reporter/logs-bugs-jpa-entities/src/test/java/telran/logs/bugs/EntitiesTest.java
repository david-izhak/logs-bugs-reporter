package telran.logs.bugs;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import telran.logs.bugs.jpa.entities.Artifact;
import telran.logs.bugs.jpa.entities.Bug;
import telran.logs.bugs.jpa.entities.BugStatus;
import telran.logs.bugs.jpa.entities.OpeningMethod;
import telran.logs.bugs.jpa.entities.Programmer;
import telran.logs.bugs.jpa.entities.Seriousness;

@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@ContextConfiguration(classes = { ProgrammersRepo.class, ArtifactsRepo.class, BugsRepo.class })
class EntitiesTest {

	@Autowired
	ProgrammersRepo programmers;

	@Autowired
	ArtifactsRepo artifacts;

	@Autowired
	BugsRepo bugs;
	
	Programmer programmer = new Programmer(123, "Moshe", "moshe@mail.com");
	

	@Test
	void bugCreationTest() {
		programmers.save(programmer);
		Artifact artifact = new Artifact("authentication", programmer);
		artifacts.save(artifact);
		Bug bug = Bug.builder()
				.description("description")
				.dateOpen(LocalDate.now())
				.dateClose(LocalDate.now())
				.status(BugStatus.ASSIGNED)
				.seriousness(Seriousness.MINOR)
				.openningMethod(OpeningMethod.AUTOMATIC)
				.programmer(programmer).build();
		bugs.save(bug);

		List<Bug> bugsRes = bugs.findAll();

		assertEquals(1, bugsRes.size());
		assertEquals(bug, bugsRes.get(0));
	}

	@Test
	void bugCreationNullDescriptionTest() {
		assertThrows(DataIntegrityViolationException.class, () -> {
			Bug bug = Bug.builder()
				.description(null)
				.dateOpen(LocalDate.now())
				.dateClose(LocalDate.now())
				.status(BugStatus.ASSIGNED)
				.seriousness(Seriousness.MINOR)
				.openningMethod(OpeningMethod.AUTOMATIC)
				.programmer(programmer)
				.build();
			bugs.save(bug);
		});
	}

	@Test
	void bugCreationNullDateOpenTest() {
		assertThrows(DataIntegrityViolationException.class, () -> {
			Bug bug = Bug.builder()
					.description("description")
					.dateOpen(null)
					.dateClose(LocalDate.now())
					.status(BugStatus.ASSIGNED)
					.seriousness(Seriousness.MINOR)
					.openningMethod(OpeningMethod.AUTOMATIC)
					.programmer(programmer)
					.build();
			bugs.save(bug);
		});
	}

	@Test
	void bugCreationNullSeriousnessTest() {
		assertThrows(DataIntegrityViolationException.class, () -> {
			Bug bug = Bug.builder()
					.description("description")
					.dateOpen(LocalDate.now())
					.dateClose(LocalDate.now())
					.status(null)
					.seriousness(Seriousness.MINOR)
					.openningMethod(OpeningMethod.AUTOMATIC)
					.programmer(programmer)
					.build();
			bugs.save(bug);
		});
	}
	
	@Test
	void bugCreationNullBugStatusTest() {
		assertThrows(DataIntegrityViolationException.class, () -> {
			Bug bug = Bug.builder()
					.description("description")
					.dateOpen(LocalDate.now())
					.dateClose(LocalDate.now())
					.status(BugStatus.ASSIGNED)
					.seriousness(null)
					.openningMethod(OpeningMethod.AUTOMATIC)
					.programmer(programmer)
					.build();
			bugs.save(bug);
		});
	}
	
	@Test
	void bugCreationNullOpenningMethodTest() {
		assertThrows(DataIntegrityViolationException.class, () -> {
			Bug bug = Bug.builder()
					.description("description")
					.dateOpen(LocalDate.now())
					.dateClose(LocalDate.now())
					.status(BugStatus.ASSIGNED)
					.seriousness(Seriousness.MINOR)
					.openningMethod(null)
					.programmer(programmer)
					.build();
			bugs.save(bug);
		});
	}
}
