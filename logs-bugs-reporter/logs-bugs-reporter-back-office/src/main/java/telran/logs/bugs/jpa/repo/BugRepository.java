package telran.logs.bugs.jpa.repo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import telran.logs.bugs.dto.BugStatus;
import telran.logs.bugs.dto.EmailBugsCount;
import telran.logs.bugs.jpa.entities.Bug;

public interface BugRepository extends JpaRepository<Bug, Long> {

	List<Bug> findByProgrammerId(long programmerId);
	
	List<Bug> findByStatus(BugStatus status);
	
	List<Bug> findByStatusNotAndDateOpenBefore(BugStatus status, LocalDate dateOpen);
	
	@Query("SELECT programmer.email AS email, COUNT(b) AS count FROM Bug b RIGHT JOIN b.programmer AS programmer GROUP BY programmer.email ORDER BY COUNT(b) DESC")
	List<EmailBugsCount> emailBugsCounts();

	@Query(value = "select name from programmer p left join bugs b on p.id=programmer_id group by name order by count(b) desc limit :nProgrammer", nativeQuery = true)
	List<String> programmersMostBugs(int nProgrammer);

	@Query(value = "select name from programmer p left join bugs b on p.id=programmer_id group by name order by count(b) limit :nProgrammer", nativeQuery = true)
	List<String> programmersLeastBugs(int nProgrammer);

}
