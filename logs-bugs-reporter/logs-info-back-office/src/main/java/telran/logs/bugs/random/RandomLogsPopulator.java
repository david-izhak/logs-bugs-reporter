package telran.logs.bugs.random;

//import java.util.ArrayList;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import telran.logs.bugs.mongo.doc.LogDoc;
import telran.logs.bugs.repo.LogRepository;

@Component
@Slf4j
public class RandomLogsPopulator {

	@Value("${app-population-enable:false}")
	boolean flagPopulation;

	@Value("${app-number-logs:0}")
	int nLogs;

	@Autowired
	RandomLogs randomLogs;

	@Autowired
	LogRepository logRepository;

//	@PostConstruct
//	void populatingDb() {
//		if (flagPopulation) {
//			log.info("===> population started...");
//			ArrayList<LogDoc> logs = getRandomLogs(nLogs);
//			logRepository.saveAll(logs).buffer().blockFirst();
//			log.info("===> saved {} documents", logs.size());
//		}
//	}
//
//	private ArrayList<LogDoc> getRandomLogs(int nLogs2) {
//		ArrayList<LogDoc> res = new ArrayList<>();
//		for (int i = 0; i < nLogs2; i++) {
//			res.add(new LogDoc(randomLogs.createRandomLog()));
//		}
//		return res;
//	}

	@PostConstruct
	void populatingDb() {
		log.info("===> population started... Should be saved {} logs", nLogs);
		logRepository.saveAll(Flux.create(sink -> {
			for (int i = 0; i < nLogs; i++) {
				sink.next(new LogDoc(randomLogs.createRandomLog()));
			}
			sink.complete();
		})).blockLast();
		log.debug("===> Saved {} logs", logRepository.count().block());
	}
}
