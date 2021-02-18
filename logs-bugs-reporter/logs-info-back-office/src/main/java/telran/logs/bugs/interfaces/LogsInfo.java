package telran.logs.bugs.interfaces;

import reactor.core.publisher.Flux;
import telran.logs.bugs.dto.ArtifactCount;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.dto.LogTypeCount;


public interface LogsInfo {
	
	Flux<LogDto> getAllLogs();
	Flux<LogDto> getAllExceptions();
	Flux<LogDto> getLogsType(LogType logType);
	
	Flux<LogTypeCount> getLogTypeOccurrences();
	Flux<LogType> getMostEncounteredExceptionType(int nExceptions);
	Flux<ArtifactCount> getArtifactOccurrences(int nExceptions);
	Flux<String> getMostEncounterdArtifacts(int nArtifacts);
	
}