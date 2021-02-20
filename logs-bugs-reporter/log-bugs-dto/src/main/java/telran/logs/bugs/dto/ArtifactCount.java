package telran.logs.bugs.dto;

import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
public class ArtifactCount {
	public static final String ARTIFACT = "artifact";
	public String artifact;
	public long count;
}
