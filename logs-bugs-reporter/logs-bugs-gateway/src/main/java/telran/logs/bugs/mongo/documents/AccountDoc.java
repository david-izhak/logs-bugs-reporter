package telran.logs.bugs.mongo.documents;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class AccountDoc {

	String userName;
	String password;
	String[] roles;
	long activationTimestamp;
	long expirationTimestamp;
}
