package telran.logs.bugs.interfaces;

import java.time.LocalDate;

import telran.logs.bugs.dto.BugStatus;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.dto.OpeningMethod;
import telran.logs.bugs.dto.Seriousness;
import telran.logs.bugs.jpa.entities.Programmer;

public interface LogDtoToBugConverterInterface {
	public void takeLogDtoAndOpenBug(LogDto logDto);
	OpeningMethod getOpenningMethod();
	LocalDate getDateClose();
	LocalDate getDateOpen();
	String getDescription(LogDto logDto);
	BugStatus getBugStatus(Programmer programmer);
	Seriousness getSeriousness(LogType logType);
	Programmer getProgrammer(String artifact);
}
