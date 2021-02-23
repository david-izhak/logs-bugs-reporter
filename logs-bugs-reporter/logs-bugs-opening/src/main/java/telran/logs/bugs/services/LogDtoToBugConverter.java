package telran.logs.bugs.services;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import lombok.extern.slf4j.Slf4j;
import telran.logs.bugs.ArtifactsRepo;
import telran.logs.bugs.BugsRepo;
import telran.logs.bugs.dto.BugStatus;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.dto.OpeningMethod;
import telran.logs.bugs.dto.Seriousness;
import telran.logs.bugs.jpa.entities.Artifact;
import telran.logs.bugs.jpa.entities.Bug;
import telran.logs.bugs.jpa.entities.Programmer;

@Controller
@Slf4j
public class LogDtoToBugConverter implements LogDtoToBugConverterInterface {

	@Autowired
	ArtifactsRepo artifacts;

	@Autowired
	BugsRepo bugs;

	public void takeLogDtoAndOpenBug(LogDto logDto) {
		log.debug(">>>> resived LogDto {}", logDto.toString());
		Programmer programmer = getProgrammer(logDto.artifact);
		Bug bug = Bug.builder().description(getDescription(logDto)).dateOpen(getDateOpen()).dateClose(getDateClose())
				.status(getBugStatus(programmer)).seriousness(getSeriousness(logDto.logType))
				.openningMethod(getOpenningMethod()).programmer(programmer).build();
		log.debug(">>>> created bug {}", bug.toString());
		bugs.save(bug);
		log.debug(">>>> bug was saved to repo");
	}

	public OpeningMethod getOpenningMethod() {
		return OpeningMethod.AUTOMATIC;
	}

	public LocalDate getDateClose() {
		return null;
	}

	public LocalDate getDateOpen() {
		return LocalDate.now();
	}

	public String getDescription(LogDto logDto) {
		return logDto.logType + " " + logDto.result;
	}

	public BugStatus getBugStatus(Programmer programmer) {
		return programmer != null ? BugStatus.ASSIGNED : BugStatus.OPENND; // short variant (no logging)
		
		// long variant with logging
//		if (programmer != null) {
//			log.debug(">>>> return BugStatus.ASSIGNED");
//			return BugStatus.ASSIGNED;
//		}
//		log.debug(">>>> return BugStatus.OPENND");
//		return BugStatus.OPENND;
	}

	public Seriousness getSeriousness(LogType logType) {
		switch (logType) {
		case AUTHENTICATION_EXCEPTION:
			return Seriousness.BLOCKING;
		case AUTHORIZATION_EXCEPTION:
		case SERVER_EXCEPTION:
			return Seriousness.CRITICAL;
		case BAD_REQUEST_EXCEPTION:
		case NOT_FOUND_EXCEPTION:
		case DUPLICATED_KEY_EXCEPTION:
			return Seriousness.MINOR;
		default:
			return null;
		}
	}

	public Programmer getProgrammer(String artifact) {
		Artifact artifactEntity = artifacts.findById(artifact).orElse(null);
		return artifactEntity == null ? null : artifactEntity.getProgrammer(); // short variant (no logging)

		// long variant with logging
//		if (artifactEntity == null) {
//			log.debug(">>>> No Programmer, return null");
//			return null;
//		}
//		log.debug(">>>> Artifact is found {}", artifactEntity);
//		Programmer programmer = artifactEntity.getProgrammer();
//		log.debug(">>>> From Artifact got Programmer {}", programmer);
//		return programmer;
	}
}
