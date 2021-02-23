package telran.logs.bugs.dto;

import java.time.LocalDate;

import javax.validation.constraints.Min;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper=false)
@ToString
public class BugAssignDto extends BugDto {

	public BugAssignDto(Seriousness seriosness, String description, LocalDate dateOpen, @Min(1) long programmerId) {
		super(seriosness, description, dateOpen);
		this.programmerId = programmerId;
	}

	@Min(1)
	public long programmerId;
}
