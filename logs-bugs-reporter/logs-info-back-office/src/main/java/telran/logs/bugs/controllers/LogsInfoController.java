package telran.logs.bugs.controllers;

import java.util.List;

import javax.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import telran.logs.bugs.api.LogsInfoApi;
import telran.logs.bugs.dto.ArtifactCount;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.dto.LogTypeCount;
import telran.logs.bugs.interfaces.LogsInfo;

@RestController
@Slf4j
@Validated
public class LogsInfoController implements LogsInfoApi {

	@Autowired
	LogsInfo logsInfo;

	@GetMapping(value=LOGS, produces=MEDIATYPE_STREAM_JSON)
	Flux<LogDto> getAllLogs() {
		Flux<LogDto> result = logsInfo.getAllLogs();
		log.debug("Logs sent to a client");
		return result;
	}
	
	@GetMapping(value=LOGS_TYPE)
	Flux<LogDto> getLogsByTypes(@RequestParam (name=TYPE) LogType logType) {
		Flux<LogDto> result = logsInfo.getLogsType(logType);
		log.debug("Logs of type {} sent to a client", logType);
		return result;
	}

	@GetMapping(value=LOGS_EXCEPTIONS)
	Flux<LogDto> getAllExceptionsLogs() {
		Flux<LogDto> result = logsInfo.getAllExceptions();
		log.debug("Logs of all exceptions sent to a client");
		return result;
	}
	
	@GetMapping(value=LOGS_DISTRIBUTION)
	Flux<LogTypeCount> getLogTypeOccurrencies() {
		return logsInfo.getLogTypeOccurrences();
	}

	@GetMapping(value=LOGS_MOSTENCOUNTERED_EXCEPTION_TYPES)
	Flux<LogType> getMostEncounteredExceptionTypes(@RequestParam (name=N_TYPES) @Min(1) int nTypes) {
		return logsInfo.getMostEncounteredExceptionTypes(nTypes);
	}

	@GetMapping(value=LOGS_ARTIFACT_DISTRIBUTION)
	Flux<ArtifactCount> getArtifactOccurrences() {
		return logsInfo.getArtifactOccurrences();
	}
	
	@GetMapping(value=LOGS_MOSTENCOUNTERED_ARTIFACTS)
//	Flux<String> getMostEncounterdArtifacts(@RequestParam (name="n_artifacts") int nArtifacts) {
//		return logsInfo.getMostEncounterdArtifacts(nArtifacts);
	Mono<List<String>>  getMostEncounterdArtifacts(@RequestParam (name=N_ARTIFACTS) @Min(1) int nArtifacts) {
		return logsInfo.getMostEncounterdArtifacts(nArtifacts).collectList();
	}
}
