package com.rocksoft.myretail.controller

import javax.servlet.http.HttpServletResponse

import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.web.server.ResponseStatusException

import com.rocksoft.myretail.connector.RedSkyConnector
import com.rocksoft.myretail.domain.PriceInfo
import com.rocksoft.myretail.domain.Product
import com.rocksoft.myretail.repository.PriceInfoRepository

import spock.lang.Specification


class ProductControllerSpec extends Specification {
	def "GET request to products/{id} combines RedSky and Database information for product"() {
		setup:
		PriceInfoRepository mockRepository = Mock()
		RedSkyConnector mockConnector = Mock()
		ProductController controller = new ProductController(priceInfoRepository: mockRepository,
				redSkyConnector: mockConnector)

		when:
		Product result = controller.findByProductById(12345)

		then:
		result == new Product(name: 'FooBar', currentPrice: new PriceInfo(value: 12.34, currencyCode: 'FOO'))
		1 * mockRepository.findByProductId(12345) >> new PriceInfo(value: 12.34, currencyCode: 'FOO')
		1 * mockConnector.findById(12345) >> new Product(name: 'FooBar')
	}

	def "Missing product or price info triggers 404"() {
		setup:
		PriceInfoRepository mockRepository = Mock()
		RedSkyConnector mockConnector = Mock()
		ProductController controller = new ProductController(priceInfoRepository: mockRepository,
				redSkyConnector: mockConnector)

		when:
		controller.findByProductById(12345)

		then:
		ResponseStatusException notFoundException = thrown()
		1 * mockRepository.findByProductId(12345) >> null
		notFoundException.status == HttpStatus.NOT_FOUND

		when:
		controller.findByProductById(12345)

		then:
		notFoundException = thrown()
		1 * mockRepository.findByProductId(12345) >> new PriceInfo()
		1 * mockConnector.findById(12345) >> null
		notFoundException.status == HttpStatus.NOT_FOUND
	}

	def 'PUT to products/{id}/priceInfo inserts new price info with product ID, responds with HTTP 201'() {
		setup:
		PriceInfoRepository mockRepository = Mock()
		RedSkyConnector mockConnector = Mock()
		ProductController controller = new ProductController(priceInfoRepository: mockRepository,
				redSkyConnector: mockConnector)
		Product mockProduct = new Product(name: 'Foo', currentPrice: new PriceInfo(value: 12.34, currencyCode: 'FOO'))
		HttpServletResponse mockResponse = new MockHttpServletResponse()
		PriceInfo finalPriceInfo = new PriceInfo(productId:12345, value: 12.34, currencyCode: 'FOO')

		when:
		controller.updateProductPrice(12345, mockProduct, mockResponse)

		then:
		1 * mockConnector.findById(12345) >> new Product(name: 'Foo')
		1 * mockRepository.findByProductId(12345) >> null
		1 * mockRepository.save(finalPriceInfo)
		mockResponse.status == 201
	}

	def 'PUT request to products/{id}/priceInfo updates existing price info, and responds with HTTP 204'() {
		setup:
		PriceInfoRepository mockRepository = Mock()
		RedSkyConnector mockConnector = Mock()
		ProductController controller = new ProductController(priceInfoRepository: mockRepository,
				redSkyConnector: mockConnector)
		Product mockProduct = new Product(name: 'Foo', currentPrice: new PriceInfo(value: 12.34, currencyCode: 'FOO'))
		HttpServletResponse mockResponse = new MockHttpServletResponse()

		when:
		controller.updateProductPrice(12345, mockProduct, mockResponse)

		then:
		1 * mockConnector.findById(12345) >> new Product(name: 'Foo')
		1 * mockRepository.findByProductId(12345) >> mockProduct.currentPrice
		1 * mockRepository.save(mockProduct.currentPrice)
		mockResponse.status == 204
	}

	def 'Throws exception to create 404 status when product is not found'() {
		setup:
		RedSkyConnector mockConnector = Mock()
		ProductController controller = new ProductController(redSkyConnector: mockConnector)
		Product mockProduct = new Product(name: 'Foo')

		when:
		controller.updateProductPrice(12345, mockProduct, new MockHttpServletResponse())

		then:
		1 * mockConnector.findById(12345) >> null
		ResponseStatusException thrownException = thrown()
		thrownException.status == HttpStatus.NOT_FOUND
	}

	def 'Throws exception to create 400 status when price info is missing or incomplete'() {
		setup:
		RedSkyConnector mockConnector = Mock()
		ProductController controller = new ProductController(redSkyConnector: mockConnector)

		when:
		Product mockProduct = new Product(name: 'Foo', currentPrice: mockPriceInfo)
		controller.updateProductPrice(12345, mockProduct, new MockHttpServletResponse())

		then:
		1 * mockConnector.findById(12345) >> new Product(name: 'Foo')
		ResponseStatusException thrownException = thrown()
		thrownException.status == HttpStatus.BAD_REQUEST

		where:
		mockPriceInfo << [null, new PriceInfo(value:12.34), new PriceInfo(currencyCode: 'USD')]
	}
}
