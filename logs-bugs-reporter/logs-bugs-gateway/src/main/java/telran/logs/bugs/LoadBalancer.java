package telran.logs.bugs;

import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.client.loadbalancer.reactive.ReactiveLoadBalancer;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;

@Component
@Log4j2
public class LoadBalancer {

	@Autowired
	ReactiveLoadBalancer.Factory<ServiceInstance> loadBalancerFactory;

	public String getBaseUrl(String serviceName) {
		ReactiveLoadBalancer<ServiceInstance> rlb = loadBalancerFactory.getInstance(serviceName);
		Publisher<Response<ServiceInstance>> publisher = rlb.choose();
		Flux<Response<ServiceInstance>> chosen = Flux.from(publisher);
		Response<ServiceInstance> response = chosen.blockFirst();
		while (response == null) {
			log.debug(">>> LoadBalancer >> getBaseUrl: There is no an answer from discovery service. Response null.");
			response = chosen.blockFirst();
			try {
				log.debug(">>> LoadBalancer >> getBaseUrl: Waiting 10 sec.");
				Thread.sleep(10000);
				log.debug(">>> LoadBalancer >> getBaseUrl: Next attempt to receive an answer from discovery service.");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		ServiceInstance instance = response.getServer();
		String uri = instance.getUri().toString();
		String host = instance.getHost().toString();
		String serviceId = instance.getServiceId().toString();
		int port = instance.getPort();
		return "http://accounts-provider:" + port;
	}
}
