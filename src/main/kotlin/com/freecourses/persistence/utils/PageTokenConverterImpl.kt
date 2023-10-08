package com.freecourses.persistence.utils

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

@Component
class PageTokenConverterImpl(@Autowired @Qualifier("attributeValueMapper") private val objectMapper: ObjectMapper) : PageTokenConverter {
    override fun serialize(lastEvaluatedKey: Map<String, AttributeValue>?): ByteArray? {
        lastEvaluatedKey ?: return null
        return objectMapper.writeValueAsBytes(lastEvaluatedKey)
    }

    override fun deserialize(lastPageToken: ByteArray?): Map<String, AttributeValue>? {
        lastPageToken ?: return null
        return objectMapper.readValue(lastPageToken, objectMapper.typeFactory
            .constructMapType(Map::class.java, String::class.java, AttributeValue::class.java))
    }
}