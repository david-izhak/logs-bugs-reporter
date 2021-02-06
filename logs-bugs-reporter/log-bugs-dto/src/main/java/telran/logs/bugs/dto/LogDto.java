package telran.logs.bugs.dto;

import java.util.Date;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class LogDto {
	public LogDto(@NotNull Date dateTime, @NotNull LogType logType, @NotEmpty String artifact, @Min(0) int responseTime, 
			String result) {
		this.dateTime = dateTime;
		this.logType = logType;
		this.artifact = artifact;
		this.responseTime = responseTime;
		this.result = result;
	}

	@NotNull
	public Date dateTime;
	@NotNull
	public LogType logType;
	@NotEmpty
	public String artifact;
	@Min(0)
	public int responseTime;
	public String result;
}
