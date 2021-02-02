package telran.logs.bugs.services;

import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;

@Service
@Slf4j
public class LogsAnalyzerService {
	
	@Value("${app-binding-name:exceptions-out-0}")
	String bindingName;
	
	@Autowired
	StreamBridge streamBridge;
	
	@Bean
	Consumer<LogDto> getAnalyzerBean(){
		log.debug("Start creating of the consumer log {}", LogDto.class.getName());
		return this::analyzerMethod;
	}
	
	private void analyzerMethod(LogDto logDto){
		log.debug("recieved log {}", logDto);
		if(logDto.logType != null && logDto.logType != LogType.NO_EXCEPTION) {
			streamBridge.send(bindingName, logDto);
			log.warn("Recieved log with LogType of exception {}, and sended thru streamBridge", logDto.logType);
		}
	}
}
