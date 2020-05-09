package com.rocksoft.myretail.domain


import com.fasterxml.jackson.annotation.JsonProperty

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class PriceInfo {
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	Integer productId
	Double value
	String currencyCode
}
