package telran.logs.bugs;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class LogsAnalyzerAppl {
	
//	@Autowired
//	Config config;
	
	@Value("${app.strCommon}")
	String strCommon;
//	String strCommon = config.getValueStrCommon();
//	@Value("${app.str1}")
//	String str1;
	
	public static void main(String[] args) {
		SpringApplication.run(LogsAnalyzerAppl.class, args);
	}
	
	@PostConstruct
	private void displayProperties() {
//		String strCommon = config.getStrCommon();
//		String str1 = config.getStr1();
//		System.out.printf("strCommon: %s, str1: %s", strCommon, str1);
	}
}