package com.rocksoft.myretail.repository

import org.springframework.data.mongodb.repository.MongoRepository

import com.rocksoft.myretail.domain.PriceInfo

interface PriceInfoRepository extends MongoRepository<PriceInfo, Integer> {
	PriceInfo findByProductId(Integer productId)
}