package telran.logs.bugs.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class LogTypeCount {
	public static final String LOG_TYPE = "logType";
	public LogType logType;
	public long count;
}
