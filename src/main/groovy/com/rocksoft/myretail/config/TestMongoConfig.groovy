package com.rocksoft.myretail.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.mongodb.MongoDbFactory
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.SimpleMongoClientDbFactory

import de.bwaldvogel.mongo.MongoServer
import de.bwaldvogel.mongo.backend.memory.MemoryBackend

@Profile('test')
@Configuration
class TestMongoConfig {

	@Bean
	MongoTemplate mongoTemplate(MongoDbFactory mongoDbFactory) {
		return new MongoTemplate(mongoDbFactory)
	}

	@Bean
	MongoDbFactory mongoDbFactory(MongoServer mongoServer) {
		InetSocketAddress serverAddress = mongoServer.getLocalAddress()
		return new SimpleMongoClientDbFactory("mongodb://${serverAddress.hostName}:${serverAddress.port}/products")
	}

	@Bean(destroyMethod = "shutdown")
	MongoServer mongoServer() {
		MongoServer mongoServer = new MongoServer(new MemoryBackend())
		mongoServer.bind()
		return mongoServer
	}
}
