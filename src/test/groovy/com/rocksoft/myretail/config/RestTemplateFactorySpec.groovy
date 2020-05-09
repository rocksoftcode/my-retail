package com.rocksoft.myretail.config

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.web.client.RestTemplate

import spock.lang.Specification


class RestTemplateFactorySpec extends Specification {
	def "Creates RestTemplate using RestTemplateBuilder"() {
		setup:
		RestTemplateBuilder mockRestTemplateBuilder = Mock()
		RestTemplateFactory restTemplateFactory = new RestTemplateFactory()
		RestTemplate mockRestTemplate = new RestTemplate()

		when:
		RestTemplate result = restTemplateFactory.restTemplate(mockRestTemplateBuilder)

		then:
		result == mockRestTemplate
		1 * mockRestTemplateBuilder.build() >> mockRestTemplate
	}
}
