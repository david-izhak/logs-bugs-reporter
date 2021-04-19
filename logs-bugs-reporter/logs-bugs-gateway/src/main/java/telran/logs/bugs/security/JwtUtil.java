package telran.logs.bugs.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);

	@Value("${app-expiration-hours}")
	long expirationPeriod;

	public String generateToken(String username, String[] roles) {
		
		Date current = new Date();
		Map<String, Object> claims = new HashMap<>();
		claims.put("roles", roles);
		return Jwts.builder()
				.setSubject(username)
				.setClaims(claims)
				.setExpiration(new Date(current.getTime() + expirationPeriod * 3600000))
				.setIssuedAt(current)
				.signWith(key)
//				.compressWith(CompressionCodecs.GZIP)
				.compact();
	}
	
	public String[] validateToken(String token) {
		@SuppressWarnings("unchecked")
		List<String> listRoles = (List<String>) Jwts.parserBuilder()
				.setSigningKey(key) // throws SignatureException, if it fails to verify the JWT
				.build() // get the purser
				.parseClaimsJws(token)
				.getBody()
				.get("roles"); // get a list of roles
		return listRoles.toArray(new String[0]);// convert the list to an array
	}
} 
