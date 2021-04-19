package telran.logs.bugs;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class AuthDto {
	
	@NotEmpty
	String username;
	
	@Size(min = 8)
	String password;

}
