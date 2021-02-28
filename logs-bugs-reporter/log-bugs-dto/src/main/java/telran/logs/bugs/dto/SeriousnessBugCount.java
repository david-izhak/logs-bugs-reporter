package telran.logs.bugs.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
public class SeriousnessBugCount {
	
	public static final String SERIOUSNESS_TYPE = "seriousness";
	public Seriousness seriousness;
	public long count;
}
