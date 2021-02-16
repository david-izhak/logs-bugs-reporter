package telran.logs.bugs.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.interfaces.LogsInfo;

@RestController
@Slf4j
public class LogsInfoController {

	@Autowired
	LogsInfo logsInfo;

	@GetMapping(value="/logs", produces="application/stream+json")
	Flux<LogDto> getAllLogs() {
		Flux<LogDto> result = logsInfo.getAllLogs();
		log.debug("Logs sent to a client");
		return result;
	}
	
//	@GetMapping(value="/logs/type", produces="application/stream+json")
	@GetMapping(value="/logs/type")
	Flux<LogDto> getLogsByTypes(@RequestParam (name="type") LogType logType) {
		Flux<LogDto> result = logsInfo.getLogsType(logType);
		log.debug("Logs of type {} sent to a client", logType);
		return result;
	}

	@GetMapping(value="/logs/exceptions")
	Flux<LogDto> getAllExceptionsLogs() {
		Flux<LogDto> result = logsInfo.getAllExceptions();
		log.debug("Logs of all exceptions sent to a client");
		return result;
	}
}
