package com.example.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.MongoDatabaseFactory
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper
import org.springframework.data.mongodb.core.convert.MappingMongoConverter
import org.springframework.data.mongodb.core.convert.MongoCustomConversions
import org.springframework.data.mongodb.core.mapping.MongoMappingContext
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver

@Configuration
class MongoConfig {

    @Bean
    fun mappingMongoConverter(
        factory: MongoDatabaseFactory,
        context: MongoMappingContext,
        conversions: MongoCustomConversions
    ): MappingMongoConverter {
        val resolver = DefaultDbRefResolver(factory)
        val converter = MappingMongoConverter(resolver, context)
        converter.setCustomConversions(conversions)
        converter.setTypeMapper(DefaultMongoTypeMapper(null)) // ⬅️ esto elimina _class
        return converter
    }
}
