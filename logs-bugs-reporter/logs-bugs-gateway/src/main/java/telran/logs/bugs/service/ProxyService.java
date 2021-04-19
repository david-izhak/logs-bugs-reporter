package telran.logs.bugs.service;

import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.webflux.ProxyExchange;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Component
@Log4j2
public class ProxyService {
	
	@Value("${app-services-allowed: reporter-back-office:8484, logs-info-back-office:8282}")
	List<String> allowedServices;
	
	HashMap<String, String> mapServices; // key - service name, value - base URL
	
	@Value("${app-localhost:false}")
	boolean isLocalHost;
	

	public Mono<ResponseEntity<byte[]>> proxyRun(ProxyExchange<byte[]> proxy, ServerHttpRequest request) {
		String uri = request.getURI().toString();
		log.debug("received request: {}", uri);
		String serviceName = uri.split("/+")[2];
		log.debug("serviceName: {}", serviceName);
		String res = mapServices.get(serviceName);
		if (res != null) {
			int indexService = uri.indexOf(serviceName) + serviceName.length();
			res += uri.substring(indexService);
			log.debug(">>>> LogsBugsGatewayAppl > getProxiedUriserviceName > res: {}", res);
			switch (request.getMethod()) {
			case POST:
				return proxy.uri(res).post();
			case GET:
				return proxy.uri(res).get();
			case PUT:
				return proxy.uri(res).put();
			case DELETE:
				return proxy.uri(res).delete();
			default:
				return Mono.just(ResponseEntity.status(600).body("Bad request".getBytes()));
			}
		}
		return Mono.just(ResponseEntity.status(404).body("Service not found".getBytes()));
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
}
