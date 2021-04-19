package telran.logs.bugs;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.webflux.ProxyExchange;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import telran.logs.bugs.security.JwtUtil;
import telran.logs.bugs.security.SecurityConfiguration;
import telran.logs.bugs.service.ProxyService;

@SpringBootApplication
@RestController
@Log4j2
public class LogsBugsGatewayAppl {

	@Value("${app-services-allowed: reporter-back-office:8484, logs-info-back-office:8282}")
	List<String> allowedServices;

	HashMap<String, String> mapServices; // key - service name, value - base URL

	@Value("${app-localhost:false}")
	boolean isLocalHost;
	
	@Autowired
	ProxyService proxyService;
	
	@Autowired
	JwtUtil jwtUtil;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	SecurityConfiguration securityConfiguration;

	public static void main(String[] args) {
		SpringApplication.run(LogsBugsGatewayAppl.class, args);
	}

	@PostConstruct
	private void fillMapServices() {
		log.debug("app-localhost: {}", isLocalHost);
		mapServices = new HashMap<>();
		allowedServices.forEach(s -> {
			String[] servicesTokens = s.split(":");
			String serviceName = servicesTokens[0];
			String port = servicesTokens[1];
			String baseUrl = String.format("http://%s:%s", isLocalHost ? "localhost" : serviceName, port);
			mapServices.put(serviceName, baseUrl);
		});
		log.debug(">>>> LogsBugsGatewayAppl > fillMapServices: mapServices: {}", mapServices);
	}

	
	@PostMapping("/login")
	public Mono<ResponseEntity<String>> login(@RequestBody AuthDto authDto) {
		log.debug(">>>> LogsBugsGatewayAppl > login: received PostMapping /login with AuthDto: {}", authDto);
		UserDetails userDetails = securityConfiguration.mapUserDetails.get(authDto.username);
		if(userDetails == null || !passwordEncoder.matches(authDto.password, userDetails.getPassword())) {
			return Mono.just(ResponseEntity.badRequest().body("Wrong credentials"));
		}
		return Mono.just(ResponseEntity.ok(jwtUtil.generateToken(userDetails.getUsername(), userDetails.getAuthorities().stream().map(a -> a.getAuthority()).toArray(String[]::new))));
	}
	
//	@PreAuthorize(value = "hasRole('ADMIN')") // This is only example of annotation PreAuthorize. It dosn't have any meaning for this case.
	@PostMapping("/**")
	public Mono<ResponseEntity<byte[]>> postRequestProxy(ProxyExchange<byte[]> proxy, ServerHttpRequest request) {
		log.debug(">>>> LogsBugsGatewayAppl > postRequestProxy: received PostMapping /**");
		return proxyService.proxyRun(proxy, request);
	}

	@GetMapping("/**")
	public Mono<ResponseEntity<byte[]>> getRequestProxy(ProxyExchange<byte[]> proxy, ServerHttpRequest request) {
		return  proxyService.proxyRun(proxy, request);
	}

	@PutMapping("/**")
	public Mono<ResponseEntity<byte[]>> putRequestProxy(ProxyExchange<byte[]> proxy, ServerHttpRequest request) {
		return  proxyService.proxyRun(proxy, request);
	}
}
