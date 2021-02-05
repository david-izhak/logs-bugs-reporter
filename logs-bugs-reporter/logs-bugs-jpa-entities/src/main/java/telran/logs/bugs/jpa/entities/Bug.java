package telran.logs.bugs.jpa.entities;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "bugs")
@NoArgsConstructor
@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@EqualsAndHashCode
public class Bug {

	@Id
	@GeneratedValue
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	long id;

	@Column(nullable = false)
	String description;

	@Column(name = "date_open", nullable = false)
	LocalDate dateOpen;

	@Column(name = "date_close", nullable = true)
	LocalDate dateClose;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	BugStatus status;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	Seriousness seriousness;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "openning_method", nullable = false)
	OpeningMethod openningMethod;
	
	@ManyToOne
	@JoinColumn(name = "programmer_id", nullable = true)
	Programmer programmer;
}
