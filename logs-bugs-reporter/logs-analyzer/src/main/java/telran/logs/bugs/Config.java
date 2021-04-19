package telran.logs.bugs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Backoff;

import lombok.Getter;
import lombok.Setter;

//@ConfigurationProperties(prefix = "app")
//@Configuration
public class Config {
	
	private String strCommon;
	private String str1;
	
//	@Retryable(value = { IllegalStateException.class }, maxAttempts = 100, backoff = @Backoff(delay = 1000))
	public String getStrCommon() {
		return strCommon;
	}
	public void setStrCommon(String strCommon) {
		this.strCommon = strCommon;
	}
	public String getStr1() {
		return str1;
	}
	public void setStr1(String str1) {
		this.str1 = str1;
	}
	
}
