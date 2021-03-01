package telran.logs.bugs.impl;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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

	@Override
	@Transactional
	public ProgrammerDto addProgrammer(ProgrammerDto programmerDto) {
		// FIXME exceptions handling and key duplication check
		programmerRepository.save(new Programmer(programmerDto.Id, programmerDto.name, programmerDto.email));
		return programmerDto;
	}

	@Override
	public ArtifactDto addArtifact(ArtifactDto artifactDto) {
		// FIXME exceptions handling and key duplication check
		Programmer programmer = programmerRepository.findById(artifactDto.programmer).orElse(null);
		artifactRepository.save(new Artifact(artifactDto.artifactId, programmer));
		return artifactDto;
	}

	@Override
	@Transactional
	public BugResponseDto openBug(BugDto bugDto) {
		LocalDate dateOpen = bugDto.dateOpen != null ? bugDto.dateOpen : LocalDate.now();
		// FIXME exceptions handling
		Bug bug = new Bug(bugDto.description, dateOpen , null, BugStatus.OPENND, bugDto.seriosness,
				 OpeningMethod.MANUAL, null);
		bugRepository.save(bug);
		return toBugResponseDto(bug);
	}

	private BugResponseDto toBugResponseDto(Bug bug) {
		Programmer programmer = bug.getProgrammer();
		long programmerId = programmer == null ?  0 : programmer.getId(); // Is 0 possible? In class BugAssignDto is annotation @Min(1) over the field programmerId;
		return new BugResponseDto(bug.getId(), bug.getSeriousness(), bug.getDescription(), bug.getDateOpen(), programmerId , bug.getDateClose(), bug.getStatus(), bug.getOpenningMethod());
	}

	@Override
	@Transactional
	public BugResponseDto openBugAndAssignBug(BugAssignDto bugDto) {
		LocalDate dateOpen = bugDto.dateOpen != null ? bugDto.dateOpen : LocalDate.now();
		// FIXME exceptions handling
		Programmer programmer = programmerRepository.findById(bugDto.programmerId).orElse(null);
		// TODO exceptions in the case programmer is null
		Bug bug = new Bug(bugDto.description, dateOpen, null, BugStatus.ASSIGNED, 
				bugDto.seriosness, OpeningMethod.MANUAL, programmer);
		bug = bugRepository.save(bug);
		return toBugResponseDto(bug);
	}

	@Override
	@Transactional
	public void assignBug(AssignBugData assignData) {
		// FIXME exceptions handling
		Bug bug = bugRepository.findById(assignData.bugId).orElse(null);
		bug.setDescription(bug.getDescription() + "\nAssignment Description: " + 
		assignData.description);
		Programmer programmer = programmerRepository.findById(assignData.programmerId).orElse(null);
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
		// FIXME exceptions handling
		Bug bug = bugRepository.findById(closeData.bugId).orElse(null);
		bug.setDescription(bug.getDescription() + "\nClose Description: " + closeData.description);
		bug.setStatus(BugStatus.CLOSED);
		bug.setDateClose(closeData.dateClose);
		log.debug("====> bug with index 0 is {}", bugRepository.findAll().get(0));
	}

	@Override
	public List<BugResponseDto> getUnClosedBugsMoreDuration(int days) {
		LocalDate dateOpen = LocalDate.now().minusDays(days);
		List<Bug> bugs = bugRepository.findByStatusNotAndDateOpenBefore(BugStatus.CLOSED, dateOpen);
		return toListBugResponseDto(bugs );
	}

	@Override
	public List<BugResponseDto> getBugsProgrammer(long programmerId) {
		List<Bug> bugs = bugRepository.findByProgrammerId(programmerId);
		return bugs.isEmpty() ? new LinkedList<>() : toListBugResponseDto(bugs);
	}

	private List<BugResponseDto> toListBugResponseDto(List<Bug> bugs) {
		return bugs.stream().map(this::toBugResponseDto).collect(Collectors.toList());
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
}