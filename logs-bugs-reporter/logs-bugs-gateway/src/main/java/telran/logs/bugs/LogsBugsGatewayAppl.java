package telran.logs.bugs;

import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.webflux.ProxyExchange;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@SpringBootApplication
@RestController
@Log4j2
public class LogsBugsGatewayAppl {
	
	@Value("${app-services-allowed: reporter-back-office:8484, logs-info-back-office:8282}")
	List<String> allowedServices;
	
	HashMap<String, String> mapServices; // key - service name, value - base URL
	
	@Value("${app-localhost:false}")
	boolean isLocalHost;
	
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
			String baseUrl = String.format("http://%s:%s", isLocalHost?"localhost":serviceName, port);
			mapServices.put(serviceName, baseUrl);
		});
		log.debug(">>>> LogsBugsGatewayAppl > fillMapServices: mapServices: {}", mapServices);
	}
	
	@PostMapping("/**")
	public Mono<ResponseEntity<byte[]>> postRequestProxy(ProxyExchange<byte[]> proxy, ServerHttpRequest request){
		String proxiedUri = getProxiedUri(request);
		if(proxiedUri == null) {
			return Mono.just(ResponseEntity.status(404).body("Service not found".getBytes()));
		}
		return proxy.uri(proxiedUri).post();
	}

	@GetMapping("/**")
	public Mono<ResponseEntity<byte[]>> getRequestProxy(ProxyExchange<byte[]> proxy, ServerHttpRequest request){
		String proxiedUri = getProxiedUri(request);
		log.debug(">>>> LogsBugsGatewayAppl > getRequestProxy > proxiedUri: {}", proxiedUri);
		if(proxiedUri == null) {
			return Mono.just(ResponseEntity.status(404).body("Service not found".getBytes()));
		}
		return proxy.uri(proxiedUri).get();
	}
	
	@PutMapping("/**")
	public Mono<ResponseEntity<byte[]>> putRequestProxy(ProxyExchange<byte[]> proxy, ServerHttpRequest request){
		String proxiedUri = getProxiedUri(request);
		if(proxiedUri == null) {
			return Mono.just(ResponseEntity.status(404).body("Service not found".getBytes()));
		}
		return proxy.uri(proxiedUri).put();
	}

	private String getProxiedUri(ServerHttpRequest request) {
		String uri = request.getURI().toString();
		log.debug("received request: {}", uri);
		String serviceName = uri.split("/+")[2];
		log.debug("serviceName: {}", serviceName);
		String res = mapServices.get(serviceName);
		if(res != null) {
			int indexService = uri.indexOf(serviceName) + serviceName.length();
			res += uri.substring(indexService);
			log.debug(">>>> LogsBugsGatewayAppl > getProxiedUriserviceName > res: {}", res);
		}
		return res;
	}
}
