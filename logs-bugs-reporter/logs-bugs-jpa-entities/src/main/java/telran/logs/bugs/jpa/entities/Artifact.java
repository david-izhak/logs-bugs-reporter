package telran.logs.bugs.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "artifacts")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class Artifact {
	
	@Id
	@Column(name = "artifact_id")
	@NonNull String artifacId;
	
	@ManyToOne
	@JoinColumn(name = "programmer_id", nullable = false)
	@NonNull Programmer programmer;
}
