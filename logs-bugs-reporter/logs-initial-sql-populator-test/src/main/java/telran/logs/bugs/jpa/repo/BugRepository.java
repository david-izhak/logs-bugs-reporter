package telran.logs.bugs.jpa.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import telran.logs.bugs.jpa.entities.Bug;

public interface BugRepository extends JpaRepository<Bug, Long> {
}
