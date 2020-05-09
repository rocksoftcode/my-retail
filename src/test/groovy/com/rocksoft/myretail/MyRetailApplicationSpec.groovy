package com.rocksoft.myretail

import org.springframework.boot.SpringApplication

import spock.lang.Specification

class MyRetailApplicationSpec extends Specification {
	def "Initializes application"() {
		setup:
		GroovyMock(SpringApplication, global: true)

		when:
		MyRetailApplication.main('some', 'args')

		then:
		1 * SpringApplication.run(MyRetailApplication, 'some', 'args')
	}
}
