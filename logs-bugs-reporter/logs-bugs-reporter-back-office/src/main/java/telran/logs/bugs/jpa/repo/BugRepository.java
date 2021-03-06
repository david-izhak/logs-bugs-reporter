package telran.logs.bugs.jpa.repo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import telran.logs.bugs.dto.BugStatus;
import telran.logs.bugs.dto.EmailBugsCount;
import telran.logs.bugs.dto.Seriousness;
import telran.logs.bugs.jpa.entities.Bug;

public interface BugRepository extends JpaRepository<Bug, Long> {

	List<Bug> findByProgrammerId(long programmerId);

	List<Bug> findByStatus(BugStatus status);

	List<Bug> findByStatusNotAndDateOpenBefore(BugStatus status, LocalDate dateOpen);

	@Query("SELECT programmer.email AS email, COUNT(b) AS count FROM Bug b RIGHT JOIN b.programmer AS programmer GROUP BY programmer.email ORDER BY COUNT(b) DESC")
	List<EmailBugsCount> emailBugsCounts();

//	Variant 1 - with native query
//	@Query(value = "select name from programmer p left join bugs b on p.id=programmer_id group by name order by count(b.*) desc limit :nProgrammer", nativeQuery = true)
//	List<String> programmersMostBugs(int nProgrammer);
//	Variant 2 - with JPQL
	@Query(value = "SELECT programmer.name as name FROM Bug b GROUP BY programmer ORDER BY COUNT(b) DESC, name ASC")
	List<String> programmersMostBugs(Pageable pageable);

//	Variant 1 - with native query
//	@Query(value = "SELECT name from programmer p left join bugs b on p.id=programmer_id group by name order by count(b.*) limit :nProgrammer", nativeQuery = true)
//	List<String> programmersLeastBugs(int nProgrammer);
//	Variant 2 - with JPQL
	@Query(value = "SELECT programmer.name AS name FROM Bug AS b RIGHT JOIN b.programmer AS programmer GROUP BY programmer ORDER BY COUNT(b) ASC, name ASC")
	List<String> programmersLeastBugs(Pageable pageable);
	
	long countBySeriousness(Seriousness s); //returns count of bugs of the given seriousness

//	Variant 1 - with native query
//	@Query(value = "SELECT seriousness FROM bugs GROUP BY seriousness ORDER BY COUNT(*) DESC LIMIT :n_types", nativeQuery = true)
//	List<Seriousness> seriousnessTypesWithMostCountOfBugs(@Param("n_types") int nTypes);
//	Variant 2 - with JPQL
	@Query(value = "SELECT seriousness FROM Bug GROUP BY seriousness ORDER BY COUNT(*) DESC")
	List<Seriousness> seriousnessTypesWithMostCountOfBugs(Pageable pageable);

}
