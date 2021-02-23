package telran.logs.bugs.repo;

import reactor.core.publisher.Flux;
import telran.logs.bugs.dto.ArtifactCount;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.dto.LogTypeCount;

public interface LogStatistics {
	Flux<LogTypeCount> getLogTypeCounts();
	Flux<LogTypeCount> getMostEncounteredExceptionTypes(int nExceptions);
	Flux<ArtifactCount> getArtifactOccurrences();
	Flux<ArtifactCount> getMostEncounterdArtifacts(int nArtifacts);
}
