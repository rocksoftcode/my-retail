package com.rocksoft.myretail.connector

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate

import com.rocksoft.myretail.domain.Product

@Component
class RedSkyConnector {
	@Value('${catalog.host}')
	String host
	@Value('${catalog.port}')
	Integer port
	@Value('${catalog.version}')
	String version
	@Value('${catalog.excludes}')
	List<String> excludes

	@Autowired
	RestTemplate restTemplate;

	static final String ENTITY_URL = 'http://{host}:{port}/{version}/pdp/tcin/{id}?excludes={excludes}'

	Product findById(Integer id) {
		Map<String, ?> urlVariables = [host: host, port: port, version: version, id: id, excludes: excludes.join(',')]
		try {
			return restTemplate.getForEntity(ENTITY_URL, Product, urlVariables)?.body
		} catch (HttpClientErrorException ignored) {
			return null
		}
	}
}
