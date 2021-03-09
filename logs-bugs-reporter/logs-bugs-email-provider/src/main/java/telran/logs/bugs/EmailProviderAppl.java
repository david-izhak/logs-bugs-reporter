package telran.logs.bugs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import telran.logs.bugs.api.EmailProviderApi;
import telran.logs.bugs.jpa.entities.Artifact;
import telran.logs.bugs.repo.ArtifactRepository;

@SpringBootApplication
@RestController
@Slf4j
public class EmailProviderAppl implements EmailProviderApi {
	
	@Autowired
	ArtifactRepository artifactsRepo;
	
	public static void main(String[] args) {
		SpringApplication.run(EmailProviderAppl.class, args);
	}
	
	@GetMapping(EMAIL_ARTIFACT)
	String getEmail (@PathVariable(name = PATHVARIABLE_NAME) String artifact) {
		log.debug("Resieved GET query with an artifact {}", artifact);
		Artifact artifactEntities = artifactsRepo.findById(artifact).orElse(null);
		String email =   artifactEntities == null ? ANSWER_IF_ARTIFACT_NO_EXISTS : artifactEntities.getProgrammer().getEmail();
		log.debug("Sent the answer {}", email);
		return  email;
	}
}
