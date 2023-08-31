package com.freecourses.persistence.utils

import com.fasterxml.jackson.databind.ObjectMapper
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

class PageTokenConverterImpl(private val objectMapper: ObjectMapper) : PageTokenConverter {
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