package com.rocksoft.myretail

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration

@SpringBootApplication(exclude = ErrorMvcAutoConfiguration)
class MyRetailApplication {

	static void main(String[] args) {
		SpringApplication.run(MyRetailApplication, args)
	}

}
