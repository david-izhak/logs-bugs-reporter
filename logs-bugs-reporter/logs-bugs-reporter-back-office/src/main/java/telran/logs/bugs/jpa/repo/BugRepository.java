package telran.logs.bugs.jpa.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import telran.logs.bugs.dto.EmailBugsCount;
import telran.logs.bugs.jpa.entities.Bug;

public interface BugRepository extends JpaRepository<Bug, Long> {

	List<Bug> findByProgrammerId(long programmerId);
	
	@Query("SELECT programmer.email as email, count(*) as count FROM Bug GROUP BY programmer.email ORDER BY count(*)")
	List<EmailBugsCount> emailBugsCounts();
}
