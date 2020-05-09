package com.rocksoft.myretail.connector


import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate

import com.rocksoft.myretail.domain.PriceInfo
import com.rocksoft.myretail.domain.Product

import spock.lang.Specification

class RedSkyConnectorSpec extends Specification {
	def "Calls RedSky endpoint using application properties"() {
		setup:
		RestTemplate mockRestTemplate = Mock()
		RedSkyConnector connector = new RedSkyConnector(restTemplate: mockRestTemplate)
		connector.host = 'foo'
		connector.port = 1234
		connector.version = 'bar'
		connector.excludes = ['field1', 'field2']
		Product mockProduct = new Product(name: 'FooBar', currentPrice: new PriceInfo(value: 12.34, currencyCode: 'FOO'))
		Map<String, ?> defaultUrlVars = [host: connector.host, port: 1234, version: connector.version, excludes: connector.excludes.join(',')]

		when:
		Product result = connector.findById(12345)

		then:
		result == mockProduct
		1 * connector.restTemplate.getForEntity(RedSkyConnector.ENTITY_URL, Product, defaultUrlVars + [id: 12345]) >> new ResponseEntity<Product>(mockProduct, HttpStatus.OK)

		when:
		Product nullResult = connector.findById(12345)

		then:
		!nullResult
		1 * connector.restTemplate.getForEntity(RedSkyConnector.ENTITY_URL, Product, defaultUrlVars + [id: 12345]) >> null
	}
}
