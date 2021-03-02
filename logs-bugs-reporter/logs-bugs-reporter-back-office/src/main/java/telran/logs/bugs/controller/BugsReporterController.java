package telran.logs.bugs.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import telran.logs.bugs.dto.ArtifactDto;
import telran.logs.bugs.dto.AssignBugData;
import telran.logs.bugs.dto.BugAssignDto;
import telran.logs.bugs.dto.BugDto;
import telran.logs.bugs.dto.BugResponseDto;
import telran.logs.bugs.dto.CloseBugData;
import telran.logs.bugs.dto.EmailBugsCount;
import telran.logs.bugs.dto.ProgrammerDto;
import telran.logs.bugs.dto.Seriousness;
import telran.logs.bugs.dto.SeriousnessBugCount;
import telran.logs.bugs.interfaces.BugsReporter;
import static telran.logs.bugs.api.BugsReporterApi.*;

@RestController
@Slf4j
@Validated
public class BugsReporterController {

	@Autowired
	BugsReporter bugsReporter;

	@PostMapping(value = PATH_BUGS_PROGRAMMERS, produces = APPLICATION_JSON_VALUE)
	public ProgrammerDto addProgrammer(@RequestBody @Valid ProgrammerDto programmerDto) {
		log.debug("Recieved Post request with ProgrammerDto {}", programmerDto);
		ProgrammerDto returnProgrammerDto = bugsReporter.addProgrammer(programmerDto);
		log.debug("Start sending newProgrammerDto {}", returnProgrammerDto);
		return returnProgrammerDto;
	}

	@PostMapping(value = PATH_BUGS_OPEN, produces = APPLICATION_JSON_VALUE)
	public BugResponseDto openBug(@RequestBody @Valid BugDto bugDto) {
		log.debug("Recieved Post request with BugDto {}", bugDto);
		BugResponseDto bugResponseDto = bugsReporter.openBug(bugDto);
		log.debug("Start sending BugResponseDto {}", bugResponseDto);
		return bugResponseDto;
	}

	@PostMapping(value = PATH_BUGS_OPEN_ASSIGN, produces = APPLICATION_JSON_VALUE)
	public BugResponseDto openAndAssignBug(@RequestBody @Valid BugAssignDto bugAssignDto) {
		log.debug("Recieved Post request with BugAssignDto {}", bugAssignDto);
		BugResponseDto bugResponseDto = bugsReporter.openAndAssignBug(bugAssignDto);
		log.debug("Start sending BugResponseDto {}", bugResponseDto);
		return bugResponseDto;
	}

	@PutMapping(value = PATH_BUGS_ASSIGN)
	public void assignBug(@RequestBody @Valid AssignBugData assignBugData) {
		log.debug("Recieved Put request with AssignBugData {}", assignBugData);
		bugsReporter.assignBug(assignBugData);
		log.debug("Method assignBug called");
	}

	@GetMapping(value = PATH_BUGS_PROGRAMMERS_GET, produces = APPLICATION_JSON_VALUE)
	public List<BugResponseDto> getBugsProgrammer(@RequestParam(name = "programmer_id") @Min(1) long programmer_id) {
		log.debug("Recieved Get request with long programmer_id {}", programmer_id);
		List<BugResponseDto> listBugResponseDto = bugsReporter.getBugsProgrammer(programmer_id);
		log.debug("Start sending List<BugResponseDto> {}", listBugResponseDto);
		return listBugResponseDto;
	}

	@GetMapping(BUGS_PROGRAMMERS_COUNT)
	public List<EmailBugsCount> getEmailBugsCount() {
		List<EmailBugsCount> result = bugsReporter.getEmailBugsCounts();
		result.forEach(ec -> log.debug("email: {}; count: {}", ec.getEmail(), ec.getCount()));
		return result;
	}

	@PostMapping(value = PATH_BUGS_ARTIFACT, produces = APPLICATION_JSON_VALUE)
	public ArtifactDto addArtifact(@RequestBody @Valid ArtifactDto artifactDto) {
		log.debug("Recieved Post request with ArtifactDto {}", artifactDto);
		ArtifactDto returnArtifactDto = bugsReporter.addArtifact(artifactDto);
		log.debug("Start sending ArtifactDto {}", returnArtifactDto);
		return returnArtifactDto;
	}

	@PutMapping(value = PATH_BUGS_CLOSE_DATA)
	public void closeBug(@RequestBody @Valid CloseBugData closeBugData) {
		log.debug("Recieved Put request with CloseBugData {}", closeBugData);
		bugsReporter.closeBug(closeBugData);
		log.debug("Method closeBug called");
	}

	@GetMapping(BUGS_MOST_N_PROGRAMMERS)
	public List<String> getProgrammersMostBugs(@RequestParam(name = "n_programmers") @Min(1) int nProgrammer) {
		log.debug("Recieved GET request of programmers with most bugs for n_programmers {}", nProgrammer);
		List<String> result = bugsReporter.getProgrammersMostBugs(nProgrammer);
		log.debug("Start sending List of programmers with most bugs {}", result);
		return result;
	}

	@GetMapping(BUGS_LEAST_N_PROGRAMMERS)
	public List<String> getProgrammersLeastBugs(@RequestParam(name = "n_programmers") @Min(1) int nProgrammer) {
		log.debug("Recieved GET request of programmers with least bugs for n_programmers {}", nProgrammer);
		List<String> result = bugsReporter.getProgrammersLeastBugs(nProgrammer);
		log.debug("Start sending List of programmers with least bugs {}", result);
		return result;
	}

	@GetMapping(BUGS_SERIOUSNESS_COUNT)
	public List<SeriousnessBugCount> getSeriousnessDistribution() {
		log.debug("Recieved GET request of distribution of bugs according seriousness");
		List<SeriousnessBugCount> result = bugsReporter.getSeriousnessDistribution();
		result.forEach(bc -> log.debug("seriousness: {}; count: {}", bc.getSeriousness(), bc.getCount())); 
		return result;
	}
	
	@GetMapping(BUGS_SERIOUSNESS_MOST)
	public List<Seriousness> getSeriousnessTypesWithMostBugs(@RequestParam(name = "n_seriousness") @Min(1) int nunberSeriousnessTypes) {
		log.debug("Recieved GET request of Seriousness types with most count of bugs");
		List<Seriousness> result = bugsReporter.getSeriousnessTypesWithMostBugs(nunberSeriousnessTypes);
		log.debug("List of seriousness types with most bugs {}; nTypes: {}", result, nunberSeriousnessTypes);
		return result;
	}
}
