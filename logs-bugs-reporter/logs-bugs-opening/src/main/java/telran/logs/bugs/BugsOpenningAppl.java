package telran.logs.bugs;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.jpa.entities.Artifact;
import telran.logs.bugs.jpa.entities.Bug;
import telran.logs.bugs.jpa.entities.BugStatus;
import telran.logs.bugs.jpa.entities.OpenningMethod;
import telran.logs.bugs.jpa.entities.Programmer;
import telran.logs.bugs.jpa.entities.Seriousness;

@SpringBootApplication
@EntityScan("telran.logs.bugs.jpa.entities")
@Slf4j
public class BugsOpenningAppl {
	
	@Autowired
	ArtifactsRepo artifacts;
	
	@Autowired
	ProgrammersRepo programmers;
	
	@Autowired
	BugsRepo bugs;
	
	public static void main(String[] args) {
		SpringApplication.run(BugsOpenningAppl.class, args);
	}
	
	@Bean
	Consumer<LogDto> getLogDtoConsumer() {
		return this::takeLogDtoAndOpenBug;
	}
	
	void takeLogDtoAndOpenBug(LogDto logDto) {
		// Должны ли мы валидировать LogDto?
		//// LogDto теоретически может содержать тип NO_EXCEPTION. Как реагировать на такую ситуацию? Сследует ли бросать Exception?
		Programmer programmer = getProgrammer(logDto.artifact);
		Bug bug = new Bug((logDto.logType + " " + logDto.result), LocalDate.now(), null, getBugStatus(programmer), getSeriousness(logDto.logType), OpenningMethod.AUTOMATIC, programmer);
		log.debug(">>>> created bug {}", bug.toString());
		bugs.save(bug);
		log.debug(">>>> bug {} saved to repo", bug.toString());
	}

	private @NonNull BugStatus getBugStatus(Programmer programmer) {
		if(programmer != null) {
			log.debug(">>>> return BugStatus.ASSIGNED");
			return BugStatus.ASSIGNED;
		}
		log.debug(">>>> return BugStatus.OPENND");
		return BugStatus.OPENND;
	}

	private @NonNull Seriousness getSeriousness(@NotNull LogType logType) {
		switch(logType) {
		case AUTHENTICATION_EXCEPTION: return Seriousness.BLOCKING;
		case AUTHORIZATION_EXCEPTION:
		case SERVER_EXCEPTION: return Seriousness.CRITICAL;
		case BAD_REQUEST_EXCEPTION:
		case NOT_FOUND_EXCEPTION:
		case DUPLICATED_KEY_EXCEPTION: return Seriousness.MINOR;
		default: break;
		}
		return null;
	}

	private @NonNull Programmer getProgrammer(@NotEmpty String artifact) {
		List<Artifact> artifactList = artifacts.findAll();
		for(Artifact artifactEntity: artifactList) {
			if(artifactEntity.getArtifacId().equalsIgnoreCase(artifact)) {
				log.debug(">>>> return Programmer");
				return artifactEntity.getProgrammer();
			}
		}
		log.debug(">>>> No Programmer, return null");
		return null;
	}
}
