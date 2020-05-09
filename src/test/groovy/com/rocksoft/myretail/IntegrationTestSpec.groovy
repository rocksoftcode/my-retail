package com.rocksoft.myretail

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus

import org.hamcrest.core.StringContains
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.web.client.RestTemplate

import com.rocksoft.myretail.domain.PriceInfo
import com.rocksoft.myretail.domain.Product

import groovy.json.JsonOutput
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(['test'])
class IntegrationTestSpec extends Specification {
	@Autowired
	TestRestTemplate restTemplate
	@Autowired
	MongoTemplate mongoTemplate
	@Autowired
	RestTemplate mockedRestTemplate
	MockRestServiceServer server

	def setup() {
		server = MockRestServiceServer.createServer(mockedRestTemplate)
	}

	def cleanup() {
		mongoTemplate.dropCollection(PriceInfo)
	}

	def 'Retrieves product by ID'() {
		setup:
		mongoTemplate.insert(new PriceInfo(productId:12345, value:12.34, currencyCode: 'USD'))
		server.expect(requestTo(StringContains.containsString('/v2/pdp/tcin/12345'))).andRespond(withSuccess(JsonOutput.toJson(new Product(id: 12345, name: 'FooBar')),
				MediaType.APPLICATION_JSON))

		when:
		Product result = restTemplate.getForEntity('/products/12345', Product).body

		then:
		result.id
		result.name
		result.currentPrice
		!result.currentPrice.productId
		result.currentPrice.value
		result.currentPrice.currencyCode
	}

	def 'Returns 404 error if product missing'() {
		setup:
		server.expect(requestTo(StringContains.containsString('/v2/pdp/tcin/12346'))).andRespond(withStatus(HttpStatus.NOT_FOUND))

		when:
		ResponseEntity<String> response = restTemplate.getForEntity('/products/12346', String)

		then:
		response.statusCode == HttpStatus.NOT_FOUND
	}

	def 'Updates price info on product'() {
		setup:
		server.expect(requestTo(StringContains.containsString('/v2/pdp/tcin/12345'))).andRespond(withSuccess(JsonOutput.toJson(new Product(id: 12345, name: 'FooBar')),
				MediaType.APPLICATION_JSON))

		when:
		Product mockProduct = new Product(id:12345, name: 'Foo', currentPrice: new PriceInfo(value: 12.34, currencyCode: 'USD'))
		HttpEntity<Product> requestBody = new HttpEntity<>(mockProduct)
		ResponseEntity<String> result = restTemplate.exchange('/products/12345', HttpMethod.PUT, requestBody, String)

		then:
		result.statusCode == HttpStatus.CREATED
	}

	def 'Returns 404 error if product missing on price update'() {
		setup:
		server.expect(requestTo(StringContains.containsString('/v2/pdp/tcin/12347'))).andRespond(withStatus(HttpStatus.NOT_FOUND))

		when:
		Product mockProduct = new Product(id:12347, name: 'Foo', currentPrice: new PriceInfo(value: 12.34, currencyCode: 'USD'))
		HttpEntity<Product> requestBody = new HttpEntity<>(mockProduct)
		ResponseEntity<String> result = restTemplate.exchange('/products/12347', HttpMethod.PUT, requestBody, String)

		then:
		result.statusCode == HttpStatus.NOT_FOUND
	}

	def 'Returns 400 error if prince info incomplete on price update'() {
		setup:
		server.expect(requestTo(StringContains.containsString('/v2/pdp/tcin/12348'))).andRespond(withSuccess(JsonOutput.toJson(new Product(id: 12345, name: 'FooBar')),
				MediaType.APPLICATION_JSON))

		when:
		Product mockProduct = new Product(id:12348, name: 'Foo', currentPrice: new PriceInfo(currencyCode: 'USD'))
		HttpEntity<Product> requestBody = new HttpEntity<>(mockProduct)
		ResponseEntity<String> result = restTemplate.exchange('/products/12348', HttpMethod.PUT, requestBody, String)

		then:
		result.statusCode == HttpStatus.BAD_REQUEST
	}
}
