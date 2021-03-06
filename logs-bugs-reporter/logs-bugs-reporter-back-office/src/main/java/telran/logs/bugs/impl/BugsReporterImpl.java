package telran.logs.bugs.impl;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.validation.ConstraintViolationException;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import telran.logs.bugs.dto.ArtifactDto;
import telran.logs.bugs.dto.AssignBugData;
import telran.logs.bugs.dto.BugAssignDto;
import telran.logs.bugs.dto.BugDto;
import telran.logs.bugs.dto.BugResponseDto;
import telran.logs.bugs.dto.BugStatus;
import telran.logs.bugs.dto.CloseBugData;
import telran.logs.bugs.dto.EmailBugsCount;
import telran.logs.bugs.dto.OpeningMethod;
import telran.logs.bugs.dto.ProgrammerDto;
import telran.logs.bugs.dto.Seriousness;
import telran.logs.bugs.dto.SeriousnessBugCount;
import telran.logs.bugs.exceptions.DuplicatedKeyException;
import telran.logs.bugs.exceptions.NotFoundException;
import telran.logs.bugs.interfaces.BugsReporter;
import telran.logs.bugs.jpa.entities.Artifact;
import telran.logs.bugs.jpa.entities.Bug;
import telran.logs.bugs.jpa.entities.Programmer;
import telran.logs.bugs.jpa.repo.ArtifactRepository;
import telran.logs.bugs.jpa.repo.BugRepository;
import telran.logs.bugs.jpa.repo.ProgrammerRepository;

@Service
@Slf4j
public class BugsReporterImpl implements BugsReporter {
	
	BugRepository bugRepository;
	ArtifactRepository artifactRepository;
	ProgrammerRepository programmerRepository;

	public BugsReporterImpl(BugRepository bugRepository, ArtifactRepository artifactRepository,
			ProgrammerRepository programmerRepository) {
		super();
		this.bugRepository = bugRepository;
		this.artifactRepository = artifactRepository;
		this.programmerRepository = programmerRepository;
	}

	private BugResponseDto toBugResponseDto(Bug bug) {
		Programmer programmer = bug.getProgrammer();
		long programmerId = programmer == null ?  0 : programmer.getId(); // Is 0 possible? In class BugAssignDto is annotation @Min(1) over the field programmerId;
		return new BugResponseDto(bug.getId(), bug.getSeriousness(), bug.getDescription(), bug.getDateOpen(), programmerId , bug.getDateClose(), bug.getStatus(), bug.getOpenningMethod());
	}

	private List<BugResponseDto> toListBugResponseDto(List<Bug> bugs) {
		return bugs.stream().map(this::toBugResponseDto).collect(Collectors.toList());
	}

	@Override
	@Transactional
	public ProgrammerDto addProgrammer(ProgrammerDto programmerDto) {
		boolean isProgrammer = programmerRepository.findById(programmerDto.Id).isPresent();
		if(isProgrammer) {
			throw new DuplicatedKeyException(String.format("Programmer with ID: %s, olready exsists", programmerDto.Id));
		}
		programmerRepository.save(new Programmer(programmerDto.Id, programmerDto.name, programmerDto.email));
		return programmerDto;
	}

	@Override
	public ArtifactDto addArtifact(ArtifactDto artifactDto) {
		boolean isArtifact = artifactRepository.findById(artifactDto.artifactId).isPresent();
		if(isArtifact) {
			throw new DuplicatedKeyException(String.format("Artifact with ID: %s, olready exsists", artifactDto.artifactId));
		}
		Programmer programmer = programmerRepository.findById(artifactDto.programmer).orElse(null);
		if(programmer == null) {
			throw new NotFoundException(String.format("Assigning can't be done - no programmer with ID: %s", artifactDto.programmer));
		}
		artifactRepository.save(new Artifact(artifactDto.artifactId, programmer));
		return artifactDto;
	}

	@Override
	@Transactional
	public BugResponseDto openBug(BugDto bugDto) {
		LocalDate dateOpen = bugDto.dateOpen != null ? bugDto.dateOpen : LocalDate.now();
		if(dateOpen.isAfter(LocalDate.now())) {
			throw new ConstraintViolationException(String.format("Date the bug was opened %s in the future", bugDto.dateOpen), null); // TODO null is not right. Create extension of ConstraintViolationException and replays null. 
		}
		if(dateOpen.isBefore(LocalDate.now().minusYears(20))) {
			throw new ConstraintViolationException(String.format("Date the bug was opened %s in the pust that is imposibal(more then 20 years)", bugDto.dateOpen), null);
		}
		Bug bug = new Bug(bugDto.description, dateOpen , null, BugStatus.OPENND, bugDto.seriosness,
				 OpeningMethod.MANUAL, null);
		bugRepository.save(bug);
		return toBugResponseDto(bug);
	}

	@Override
	@Transactional
	public BugResponseDto openAndAssignBug(BugAssignDto bugDto) {
		LocalDate dateOpen = bugDto.dateOpen != null ? bugDto.dateOpen : LocalDate.now();
		Programmer programmer = programmerRepository.findById(bugDto.programmerId).orElse(null);
		if(programmer == null) {
			throw new NotFoundException(String.format("Assigning can't be done - no programmer with ID: %s", bugDto.programmerId));
		}
		if(dateOpen.isAfter(LocalDate.now())) {
			throw new ConstraintViolationException(String.format("Date the bug was opened %s in the future", bugDto.dateOpen), null); // TODO null is not right. Create extension of ConstraintViolationException and replays null. 
		}
		if(dateOpen.isBefore(LocalDate.now().minusYears(20))) {
			throw new ConstraintViolationException(String.format("Date the bug was opened %s in the pust that is imposibal(more then 20 years)", bugDto.dateOpen), null);
		}
		Bug bug = new Bug(bugDto.description, dateOpen, null, BugStatus.ASSIGNED, 
				bugDto.seriosness, OpeningMethod.MANUAL, programmer);
		bug = bugRepository.save(bug);
		return toBugResponseDto(bug);
	}

	@Override
	@Transactional
	public void assignBug(AssignBugData assignData) {
		Programmer programmer = programmerRepository.findById(assignData.programmerId).orElse(null);
		if(programmer == null) {
			throw new NotFoundException(String.format("Assigning can't be done - no programmer with ID: %s", assignData.programmerId));
		}
		Bug bug = bugRepository.findById(assignData.bugId).orElse(null);
		if(bug == null) {
			throw new NotFoundException(String.format("Assigning can't be done - no bug with ID: %s", assignData.bugId));
		}
		bug.setDescription(bug.getDescription() + "\nAssignment Description: " + 
		assignData.description);
		bug.setStatus(BugStatus.ASSIGNED);
		bug.setProgrammer(programmer);
	}

	@Override
	public List<BugResponseDto> getNonAssignedBugs() {
		List<Bug> bugs = bugRepository.findByStatus(BugStatus.OPENND);
		return toListBugResponseDto(bugs);
	}

	@Override
	@Transactional
	public void closeBug(CloseBugData closeData) {
		LocalDate dateClose = closeData.dateClose != null ? closeData.dateClose : LocalDate.now();
		Bug bug = bugRepository.findById(closeData.bugId).orElse(null);
		if(bug == null) {
			throw new NotFoundException(String.format("Can't close bug - no bug with ID: %s", closeData.bugId));
		}
		bug.setDescription(bug.getDescription() + "\nClose Description: " + closeData.description);
		bug.setStatus(BugStatus.CLOSED);
		bug.setDateClose(dateClose);
	}

	@Override
	public List<BugResponseDto> getUnClosedBugsMoreDuration(int days) {  // TODO test
		LocalDate dateOpen = LocalDate.now().minusDays(days);
		List<Bug> bugs = bugRepository.findByStatusNotAndDateOpenBefore(BugStatus.CLOSED, dateOpen);
		return toListBugResponseDto(bugs );
	}

	@Override
	public List<BugResponseDto> getBugsProgrammer(long programmerId) {
		Programmer programmer = programmerRepository.findById(programmerId).orElse(null);
		if(programmer == null) {
			throw new NotFoundException(String.format("Can't get bugs - no programmer with ID: %s", programmerId));
		}
		List<Bug> bugs = bugRepository.findByProgrammerId(programmerId);
		return bugs.isEmpty() ? new LinkedList<>() : toListBugResponseDto(bugs);
	}

	@Override
	public List<EmailBugsCount> getEmailBugsCounts() {
		List<EmailBugsCount> result = bugRepository.emailBugsCounts();
		return result;
	}

	@Override
	public List<String> getProgrammersMostBugs(int nProgrammer) {
//		List<String> result = bugRepository.programmersMostBugs(nProgrammer); //		Variant 1 - with native query
		Pageable pageable = PageRequest.of(0, nProgrammer); //		Variant 2 - with JPQL
		List<String> result = bugRepository.programmersMostBugs(pageable);
		return result;
	}

	@Override
	public List<String> getProgrammersLeastBugs (int nProgrammer) {
//		List<String> result = bugRepository.programmersLeastBugs(nProgrammer); //		Variant 1 - with native query
		Pageable pageable = PageRequest.of(0, nProgrammer); //		Variant 2 - with JPQL
		List<String> result = bugRepository.programmersLeastBugs(pageable);
		return result;
	}

	@Override
	public List<SeriousnessBugCount> getSeriousnessDistribution() {
		return Arrays
				.stream(Seriousness.values())
				.map(s -> new SeriousnessBugCount(s, bugRepository.countBySeriousness(s)))
				.sorted((a, b) -> Long.compare(b.count, a.count))
				.collect(Collectors.toList());
	}

	@Override
	public List<Seriousness> getSeriousnessTypesWithMostBugs(int nunberSeriousnessTypes) {
//		List<Seriousness> result = bugRepository.seriousnessTypesWithMostCountOfBugs(nunberSeriousnessTypes);
		Pageable pageable = PageRequest.of(0, nunberSeriousnessTypes); //		Variant 2 - with JPQL
		List<Seriousness> result = bugRepository.seriousnessTypesWithMostCountOfBugs(pageable);
		return result;
	}
	
	@PostConstruct
	void populatingDb() {
		List<Programmer> programmersList = populateProgrammers();
		populateArtifacts(programmersList);
	}

	@Transactional
	private List<Programmer> populateProgrammers() {
		List<Programmer> programmersList = Arrays.asList(
				new Programmer(100, "Programmer100", "fghdfdfhgf+100@gmail.com"), 
				new Programmer(101, "Programmer101", "fghdfdfhgf+101@gmail.com"), 
				new Programmer(102, "Programmer102", "fghdfdfhgf+102@gmail.com"), 
				new Programmer(103, "Programmer103", "fghdfdfhgf+103@gmail.com"), 
				new Programmer(104, "Programmer104", "fghdfdfhgf+104@gmail.com"),
				new Programmer(105, "Programmer105", "fghdfdfhgf+105@gmail.com"), 
				new Programmer(106, "Programmer106", "fghdfdfhgf+106@gmail.com"), 
				new Programmer(107, "Programmer107", "fghdfdfhgf+107@gmail.com"), 
				new Programmer(108, "Programmer108", "fghdfdfhgf+108@gmail.com"),
				new Programmer(109, "Programmer109", "fghdfdfhgf+109@gmail.com"), 
				new Programmer(110, "Programmer110", "fghdfdfhgf+110@gmail.com"), 
				new Programmer(111, "Programmer111", "fghdfdfhgf+111@gmail.com"), 
				new Programmer(112, "Programmer112", "fghdfdfhgf+112@gmail.com"),
				new Programmer(113, "Programmer113", "fghdfdfhgf+113@gmail.com"), 
				new Programmer(114, "Programmer114", "fghdfdfhgf+114@gmail.com"), 
				new Programmer(115, "Programmer115", "fghdfdfhgf+115@gmail.com"), 
				new Programmer(116, "Programmer116", "fghdfdfhgf+116@gmail.com")
				);
		
		log.debug("populatingDb===> Start populatin programmers to DB. Shold be {}.", programmersList.size());
		programmerRepository.saveAll(programmersList);
		log.debug("populatingDb===> In DB total saved {} programmers", programmerRepository.count());
		return programmersList;
	}
	
	@Transactional
	private void populateArtifacts(List<Programmer> programmersList) {
		List<Artifact> artifactsList = Arrays.asList(
				new Artifact("class1", programmersList.get(0)),
				new Artifact("class2", programmersList.get(0)),
				new Artifact("class3", programmersList.get(0)),
				new Artifact("class4", programmersList.get(0)),
				new Artifact("class5", programmersList.get(0)),
				new Artifact("class6", programmersList.get(1)),
				new Artifact("class7", programmersList.get(1)),
				new Artifact("class8", programmersList.get(1)),
				new Artifact("class9", programmersList.get(1)),
				new Artifact("class10", programmersList.get(3)),
				new Artifact("class11", programmersList.get(3)),
				new Artifact("class12", programmersList.get(3)),
				new Artifact("class13", programmersList.get(4)),
				new Artifact("class14", programmersList.get(4)),
				new Artifact("class15", programmersList.get(5)),
				new Artifact("class16", programmersList.get(6)),
				new Artifact("class17", programmersList.get(7)),
				new Artifact("class18", programmersList.get(8)),
				new Artifact("class19", programmersList.get(9)),
				new Artifact("class20", programmersList.get(10)),
				new Artifact("authentication", programmersList.get(11)),
				new Artifact("authorization", programmersList.get(12))
				);
		
		log.debug("populatingDb===> Start populatin artifacts to DB. Shold be {}.", artifactsList.size());
		artifactRepository.saveAll(artifactsList);
		log.debug("populatingDb===> In repo total saved {} artifacts", artifactRepository.count());
	}
}
