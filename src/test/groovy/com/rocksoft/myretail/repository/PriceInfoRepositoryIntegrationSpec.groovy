package com.rocksoft.myretail.repository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.test.context.ActiveProfiles

import com.rocksoft.myretail.domain.PriceInfo

import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(['test'])
class PriceInfoRepositoryIntegrationSpec extends Specification {
	@Autowired
	MongoTemplate mongoTemplate
	@Autowired
	PriceInfoRepository priceInfoRepository

	def setup() {
		mongoTemplate.insert(new PriceInfo(productId:54321, value:43.21, currencyCode: 'EUR'))
	}

	def cleanup() {
		mongoTemplate.dropCollection(PriceInfo)
	}

	def 'Retrieves price info by product ID'() {
		expect:
		priceInfoRepository.findByProductId(54321) == new PriceInfo(productId: 54321, value:43.21, currencyCode: 'EUR')
	}
}
