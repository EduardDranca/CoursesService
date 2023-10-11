package com.freecourses.config

import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.module.SimpleModule
import com.freecourses.persistence.utils.jackson.StringAttributeValueDeserializer
import com.freecourses.persistence.utils.jackson.StringAttributeValueSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

@Configuration
open class JacksonConfig {
    @Bean
    open fun objectMapper(attributeValueSerializer: StringAttributeValueSerializer, attributeValueDeserializer: StringAttributeValueDeserializer): Module {
        val module = SimpleModule()
        module.addSerializer(AttributeValue::class.java, attributeValueSerializer)
        module.addDeserializer(AttributeValue::class.java, attributeValueDeserializer)
        return module
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