package telran.logs.bugs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import telran.logs.bugs.api.AssignerMailProviderApi;

@SpringBootApplication
@RestController
@Slf4j
public class AssignerMailProviderAppl implements AssignerMailProviderApi {
	public static void main(String[] args) {
		SpringApplication.run(AssignerMailProviderAppl.class, args);
	}
	
	@GetMapping(PATH_EMAIL_ASSIGNER)
	public String getEmail () {
		log.debug("assigner mail is {}", DEFOULT_ASSIGNER_EMAIL);
		return  DEFOULT_ASSIGNER_EMAIL == null ? "" : DEFOULT_ASSIGNER_EMAIL;
	}
}
