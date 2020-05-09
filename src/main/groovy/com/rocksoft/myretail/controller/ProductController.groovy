package com.rocksoft.myretail.controller

import javax.servlet.http.HttpServletResponse

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.server.ResponseStatusException

import com.rocksoft.myretail.connector.RedSkyConnector
import com.rocksoft.myretail.domain.PriceInfo
import com.rocksoft.myretail.domain.Product
import com.rocksoft.myretail.repository.PriceInfoRepository

@Controller
class ProductController {

	@Autowired
	PriceInfoRepository priceInfoRepository
	@Autowired
	RedSkyConnector redSkyConnector

	@RequestMapping(path = '/products/{id}', method = RequestMethod.GET, produces = ['application/json'])
	@ResponseBody
	Product findByProductById(@PathVariable('id') Integer id) {
		PriceInfo priceInfo = priceInfoRepository.findByProductId(id)
		Product product = redSkyConnector.findById(id)
		if (!priceInfo || !product) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND)
		}

		product.currentPrice = priceInfo
		return product
	}

	@RequestMapping(path = '/products/{id}', method = RequestMethod.PUT)
	void updateProductPrice(@PathVariable Integer id, @RequestBody Product product, HttpServletResponse response) {
		if (!redSkyConnector.findById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND)
		}
		if (!product.currentPrice || !product.currentPrice.value || !product.currentPrice.currencyCode) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST)
		}
		PriceInfo priceInfo = priceInfoRepository.findByProductId(id)
		HttpStatus status
		if (priceInfo) {
			priceInfo.value = product.currentPrice.value
			priceInfo.currencyCode = product.currentPrice.currencyCode
			status = HttpStatus.NO_CONTENT
		} else {
			priceInfo = product.currentPrice
			priceInfo.productId = id
			status = HttpStatus.CREATED
		}
		priceInfoRepository.save(priceInfo)
		response.status = status.value()
	}
}
