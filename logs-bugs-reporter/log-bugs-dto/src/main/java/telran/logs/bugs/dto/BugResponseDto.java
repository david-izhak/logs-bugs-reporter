package telran.logs.bugs.dto;

import java.time.LocalDate;

import javax.validation.constraints.Min;

import lombok.ToString;

@ToString
public class BugResponseDto extends BugAssignDto {

	public BugResponseDto(long bugId, Seriousness seriosness, String description, LocalDate dateOpen, @Min(1) long programmer,
			LocalDate dateClose, BugStatus status, OpeningMethod openingMethod) {
		
		super(seriosness, description, dateOpen, programmer);
		
		this.bugId = bugId;
		this.dateClose = dateClose;
		this.status = status;
		this.openingMethod = openingMethod;
	}
	
	public long bugId;
	public LocalDate dateClose;
	public BugStatus status;
	public OpeningMethod openingMethod;
}
