package telran.logs.bugs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class EmailProviderAppl {
	
	@Value("${assigner-provider-email:moshe@mail.com}")
	String assignerEmail;
	
	public static void main(String[] args) {
		SpringApplication.run(EmailProviderAppl.class, args);
	}
	
	@GetMapping("/email/assigner")
	public String getEmail () {
		return  assignerEmail == null ? "" : assignerEmail;
	}
}
