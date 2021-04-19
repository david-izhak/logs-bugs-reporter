package telran.logs.bugs.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

@Component
public class Authenticator implements ReactiveAuthenticationManager {
	
	@Autowired
	JwtUtil jwtUtil;

	@Override
	public Mono<Authentication> authenticate(Authentication authentication) {
		String authToken = authentication.getCredentials().toString();
		String[] roles = jwtUtil.validateToken(authToken);
		UsernamePasswordAuthenticationToken authenticationObject = new UsernamePasswordAuthenticationToken(null, null, AuthorityUtils.createAuthorityList(roles));
		return Mono.just(authenticationObject);
	}

}
