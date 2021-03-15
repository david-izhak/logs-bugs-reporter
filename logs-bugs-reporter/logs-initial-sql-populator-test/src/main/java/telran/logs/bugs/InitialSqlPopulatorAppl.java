package telran.logs.bugs;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import telran.logs.bugs.jpa.entities.Artifact;
import telran.logs.bugs.jpa.entities.Programmer;
import telran.logs.bugs.jpa.repo.ArtifactRepository;
import telran.logs.bugs.jpa.repo.BugRepository;
import telran.logs.bugs.jpa.repo.ProgrammerRepository;

@SpringBootApplication
@Slf4j
public class InitialSqlPopulatorAppl {
	
	private static final long TIME_OUT = 100000;
	@Value("${app-emai-prefix}")
	String emaiPrefix;
	@Value("${app-emai-postfix}")
	String emaiPostfix;
	@Value("${app-programmers-number}")
	int programmersNumber;
	@Value("${app-initial-programmer-id}")
	int initialProgrammerId;
	@Value("${app-do-populate}")
	boolean doPopulate;
	
	
	BugRepository bugRepository;
	ArtifactRepository artifactRepository;
	ProgrammerRepository programmerRepository;

	public InitialSqlPopulatorAppl(BugRepository bugRepository, ArtifactRepository artifactRepository,
			ProgrammerRepository programmerRepository) {
		super();
		this.bugRepository = bugRepository;
		this.artifactRepository = artifactRepository;
		this.programmerRepository = programmerRepository;
	}

	public static void main(String[] args) throws InterruptedException {
		ConfigurableApplicationContext ctx = SpringApplication.run(InitialSqlPopulatorAppl.class, args);
		Thread.sleep(TIME_OUT);
		ctx.close();
	}
	
	@PostConstruct
	void populatingDb() {
		if(doPopulate) {
			List<Programmer> programmersList = populateProgrammers();
			populateArtifacts(programmersList);
		}
	}

	@Transactional
	private List<Programmer> populateProgrammers() {
		log.debug("populatingDb===> Start populatin programmers to DB. Shold be {}.", programmersNumber);
		for (int i = initialProgrammerId; i < initialProgrammerId + programmersNumber; i++) {
			programmerRepository.save(new Programmer(i, "Programmer" + i, emaiPrefix + "+" + i + emaiPostfix));
		}
		log.debug("populatingDb===> In DB total saved {} programmers", programmerRepository.count());
		return programmerRepository.findAll();
	}

	@Value("${app-artifacts}")
	String[] artifacts;
	@Transactional
	private void populateArtifacts(List<Programmer> programmersList) {
		log.debug("populatingDb===> Start populatin artifacts to DB. Shold be {}.", artifacts.length);
		for(int i = 0; i < artifacts.length; i++) {
			artifactRepository.save(new Artifact(artifacts[i], programmersList.get(i > 14 ? 0 : i)));
		}
		log.debug("populatingDb===> In repo total saved {} artifacts", artifactRepository.count());
	}

}
