package telran.logs.bugs.dto;

import javax.validation.constraints.Min;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class AssignBugData {
	
	@Min(1)
	public long bugId;
	
	@Min(1)
	public long programmerId;
	
	public String description;
}
