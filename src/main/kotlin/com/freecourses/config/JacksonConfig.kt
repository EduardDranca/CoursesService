package com.freecourses.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.freecourses.persistence.utils.jackson.StringAttributeValueDeserializer
import com.freecourses.persistence.utils.jackson.StringAttributeValueSerializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

@Configuration
open class JacksonConfig {
    @Bean("attributeValueMapper")
    open fun objectMapper(@Autowired attributeValueSerializer: StringAttributeValueSerializer, @Autowired attributeValueDeserializer: StringAttributeValueDeserializer): ObjectMapper {
        val objectMapper = ObjectMapper()
        val module = SimpleModule()
        module.addSerializer(AttributeValue::class.java, attributeValueSerializer)
        module.addDeserializer(AttributeValue::class.java, attributeValueDeserializer)
        objectMapper.registerModule(module)
        return objectMapper
    }

    @Bean
    open fun attributeValueSerializer(): StringAttributeValueSerializer {
        return StringAttributeValueSerializer()
    }

    @Bean
    open fun attributeValueDeserializer(): StringAttributeValueDeserializer {
        return StringAttributeValueDeserializer()
    }
}