package telran.logs.bugs.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "artifacts")
@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@ToString
public class Artifact {
	
	@Id
	@Column(name = "artifact_id")
	@NonNull String artifacId;
	
	@ManyToOne
	@JoinColumn(name = "programmer_id", nullable = false)
	@NonNull Programmer programmer;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((artifacId == null) ? 0 : artifacId.hashCode());
		result = prime * result + ((programmer == null) ? 0 : programmer.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Artifact other = (Artifact) obj;
		if (artifacId == null) {
			if (other.artifacId != null)
				return false;
		} else if (!artifacId.equals(other.artifacId))
			return false;
		if (programmer == null) {
			if (other.programmer != null)
				return false;
		} else if (!programmer.equals(other.programmer))
			return false;
		return true;
	}
	
	
}
