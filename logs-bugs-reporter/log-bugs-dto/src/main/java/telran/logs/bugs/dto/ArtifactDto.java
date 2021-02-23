package telran.logs.bugs.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

public class ArtifactDto {
	
	@NotEmpty
	public String artifactId;
	
	@Min(1)
	public long programmer;
}
