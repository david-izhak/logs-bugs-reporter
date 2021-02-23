package telran.logs.bugs.dto;

import java.util.Date;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class LogDto {

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
