package telran.logs.bugs;

import org.reactivestreams.Publisher;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.client.loadbalancer.reactive.ReactiveLoadBalancer;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;

@Component
@Log4j2
public class LoadBalancer {
	
	ReactiveLoadBalancer.Factory<ServiceInstance> loadBalancerFactory;
	public LoadBalancer(ReactiveLoadBalancer.Factory<ServiceInstance> loadBalancerFactory) {
		log.debug(">>>> LoadBalancer > constructor");
		this.loadBalancerFactory = loadBalancerFactory;
	}
	public String getBaseUrl(String serviceName) {
		log.debug(">>>> LoadBalancer > getBaseUrl: received serviceName: {}", serviceName);
		log.debug(">>>> LoadBalancer > getBaseUrl: start method");
		ReactiveLoadBalancer<ServiceInstance> rlb = loadBalancerFactory.getInstance(serviceName);
		log.debug(">>>> LoadBalancer > getBaseUrl: rlb {}", rlb);
		Publisher<Response<ServiceInstance>> publisher = rlb.choose();
		Flux<Response<ServiceInstance>> chosen = Flux.from(publisher);
		ServiceInstance instance = chosen.blockFirst().getServer();
		log.debug(">>>> LoadBalancer > getBaseUrl: instance {}", instance);
		return instance.getUri().toString();
	}
}
