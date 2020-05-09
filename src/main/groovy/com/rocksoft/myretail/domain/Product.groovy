package com.rocksoft.myretail.domain

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class Product {
	Integer id
	String name
	PriceInfo currentPrice
}
