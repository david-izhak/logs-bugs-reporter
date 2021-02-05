package telran.logs.bugs.services;

import java.time.LocalDate;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.jpa.entities.BugStatus;
import telran.logs.bugs.jpa.entities.OpeningMethod;
import telran.logs.bugs.jpa.entities.Programmer;
import telran.logs.bugs.jpa.entities.Seriousness;

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
