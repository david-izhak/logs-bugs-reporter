package telran.logs.bugs.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table (name = "programmer")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class Programmer {
	
	@Id
	long id;

	@Column(name = "name", nullable = false)
	String name;

	@Column(name = "email", nullable = false, unique = true)
	String email;
}
