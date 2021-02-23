package telran.logs.bugs.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ProgrammerDto {
	
	@Min(1)
	public long Id;
	
	@NotEmpty
	public String name;
	
	@Email
	public String email;
	
}
