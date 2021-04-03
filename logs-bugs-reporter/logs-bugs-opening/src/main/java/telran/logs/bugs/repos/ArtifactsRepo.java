package telran.logs.bugs.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import telran.logs.bugs.jpa.entities.Artifact;

public interface ArtifactsRepo extends JpaRepository<Artifact, String> {

}
