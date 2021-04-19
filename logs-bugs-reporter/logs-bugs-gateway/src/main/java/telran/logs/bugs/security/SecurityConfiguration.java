package telran.logs.bugs.security;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;

import lombok.extern.log4j.Log4j2;
import telran.logs.bugs.api.ApiConstants;
import telran.logs.bugs.AccountsProviderClient;
import telran.logs.bugs.accounting.mongo.documents.AccountDoc;

@Configuration
@Log4j2
//@EnableWebSecurity(debug = true)
//@EnableReactiveMethodSecurity // To enable PreAuthorize
public class SecurityConfiguration {

	@Value("${security.enable}")
	boolean securityEnable;
	
	@Value("${test.mod}")
	boolean testMod;

	@Value("${app-interval-retrieve-accounts}")
	long intervalRetrieveAccounts;
	
	public Map<String, UserDetails> mapUserDetails = new ConcurrentHashMap<>();
	
	@Autowired
	AccountsProviderClient accountsProviderClient;
	
	@Autowired
	private ReactiveAuthenticationManager authenticator;
	
	@Autowired
	private ServerSecurityContextRepository securityContext;

	@Bean
	PasswordEncoder getPasswordEncoder() {
		log.debug(">>>> SecurityConfiguration: start creation of @Bean PasswordEncoder");
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	private String[] rolesMapper(String[] roles) {
		log.debug(">>>> SecurityConfiguration: mappin roles to format for creating User object: {}",
				Arrays.deepToString(roles));
		String[] rolesNew = Arrays.stream(roles).map(role -> String.format("ROLE_%s", role).toUpperCase())
				.toArray(String[]::new);
		log.debug(">>>> SecurityConfiguration: roles in format for User object: {}", Arrays.deepToString(rolesNew));
		return rolesNew;
	}

	@Bean
	MapReactiveUserDetailsService getMapDetailse() {
		log.debug(">>>> SecurityConfiguration > getMapDetailse: start creation of @Bean getMapDetailse");
		if (testMod) {
			log.debug(">>>> SecurityConfiguration: getMapDetailse: start in test mod");
			UserDetails user = new User("user", "00000000", AuthorityUtils.createAuthorityList("ROLE_USER"));
			UserDetails admin = new User("admin", "00000000", AuthorityUtils.createAuthorityList("ROLE_ADMIN"));
			UserDetails developer = new User("developer", "00000000", AuthorityUtils.createAuthorityList("ROLE_DEVELOPER"));
			UserDetails assigner = new User("assigner", "00000000", AuthorityUtils.createAuthorityList("ROLE_ASSIGNER"));
			UserDetails tester = new User("tester", "00000000", AuthorityUtils.createAuthorityList("ROLE_TESTER"));
			UserDetails project_owner = new User("project_owner", "00000000", AuthorityUtils.createAuthorityList("ROLE_PROJECT_OWNER"));
			UserDetails team_lead = new User("team_lead", "00000000", AuthorityUtils.createAuthorityList("ROLE_TEAM_LEAD"));
			UserDetails users[] = { user, admin, developer, assigner, tester, project_owner, team_lead };
			mapUserDetails = Arrays.stream(users).collect(Collectors.toConcurrentMap(UserDetails::getUsername, ud -> ud));
			return new MapReactiveUserDetailsService(mapUserDetails);
		}
		log.debug(">>>> SecurityConfiguration: getMapDetailse: start in default mod");
		mapUserDetails = getConcurrentMapFromListAccountDoc();
		log.debug(">>>> SecurityConfiguration > getMapDetailse: get ConcurrentMap of UserDetails: {}", mapUserDetails);
		return new MapReactiveUserDetailsService(mapUserDetails);
	}

	private ConcurrentMap<String, UserDetails> getConcurrentMapFromListAccountDoc() {
		List<AccountDoc> list = accountsProviderClient.getAccountsDoc();
		log.debug(">>>> SecurityConfiguration > getMapDetailse: get list of AccountDoc from repo: {}", list);
		return list.stream()
				.map(account -> new User(account.getUserName(), account.getPassword(),
							AuthorityUtils.createAuthorityList(rolesMapper(account.getRoles()))))
				.collect(Collectors.toConcurrentMap(UserDetails::getUsername, ud -> ud));
	}

	@Bean
	SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity) {
		log.debug(">>>> SecurityConfiguration: flag securityEnable is: {}", securityEnable);
		if (!securityEnable) {
			SecurityWebFilterChain filterChain = httpSecurity.csrf().disable().authorizeExchange().anyExchange()
					.permitAll().and().build();
			log.debug(">>>> SecurityConfiguration: set security to disable");
			return filterChain;
		}
		SecurityWebFilterChain filterChain = httpSecurity
				.csrf().disable()
				.httpBasic().disable()
				.cors().disable()
				.authenticationManager(authenticator)
				.securityContextRepository(securityContext)
				.authorizeExchange()
				.pathMatchers("/login").permitAll()
				.and().authorizeExchange()
				// Access to the information about all logs.
				.pathMatchers("/logs-info-back-office/**").hasRole(ApiConstants.DEVELOPER)
				// Opening bugs, as with assignment as well as without
				.pathMatchers("/reporter-back-office/bugs/open", "/reporter-back-office/bugs/open/assign").hasAnyRole(ApiConstants.ASSIGNER, ApiConstants.TESTER, ApiConstants.DEVELOPER)
				// Assignment after opening bugs
				.pathMatchers("/reporter-back-office/bugs/assign").hasRole(ApiConstants.ASSIGNER)
				// Closing bugs
				.pathMatchers("/reporter-back-office/bugs/close_bug").hasRole(ApiConstants.TESTER)
				// Adding programmer
				.pathMatchers(HttpMethod.POST, "/reporter-back-office/bugs/programmers").hasRole(ApiConstants.PROJECT_OWNER)
				// Adding artifact
				.pathMatchers("/reporter-back-office/bugs/artifact").hasAnyRole(ApiConstants.TEAM_LEAD, ApiConstants.ASSIGNER)
				.anyExchange().authenticated()
				.and().build();
		log.debug(">>>> SecurityConfiguration: set security to enable");
		return filterChain;
	}
	
	@PostConstruct
	void mapUserDetailsPolish() {
		DaemonThread daemonThread = new DaemonThread();
		daemonThread.setDaemon(true);
		daemonThread.start();
	}
	
	class DaemonThread extends Thread {
		@Override
	    public void run() {
	        while (true) {
	            try {
	                sleep(intervalRetrieveAccounts);
	            } catch (InterruptedException e) {
	            	e.printStackTrace();
	            }
	            ConcurrentMap<String, UserDetails> mapUserDetailsTemp = getConcurrentMapFromListAccountDoc();
	            mapUserDetails.clear();
	            mapUserDetails.putAll(mapUserDetailsTemp);
	        	log.debug(">>>> SecurityConfiguration > DaemonThread: set mapUserDetails from AccountsProvider: {}", mapUserDetails);
	        }
	    }
	}
}
