package com.freecourses.persistence.utils

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.freecourses.persistence.utils.jackson.StringAttributeValueDeserializer
import com.freecourses.persistence.utils.jackson.StringAttributeValueSerializer
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.net.URLEncoder
import java.util.*

@Component
class PageTokenConverterImpl : PageTokenConverter {
    override fun serialize(lastEvaluatedKey: Map<String, AttributeValue>?): String? {
        lastEvaluatedKey ?: return null
        return URLEncoder.encode(Base64.getEncoder().encodeToString(OBJECT_MAPPER.writeValueAsBytes(lastEvaluatedKey)), Charsets.UTF_8)
    }

    override fun deserialize(lastPageToken: ByteArray?): Map<String, AttributeValue>? {
        lastPageToken ?: return null
        return OBJECT_MAPPER.readValue(lastPageToken, object: TypeReference<Map<String, AttributeValue>>() {})
    }

    companion object {
        private val OBJECT_MAPPER = ObjectMapper()
        init {
            val simpleModule = SimpleModule()
            simpleModule.addSerializer(AttributeValue::class.java, StringAttributeValueSerializer())
            simpleModule.addDeserializer(AttributeValue::class.java, StringAttributeValueDeserializer())
            OBJECT_MAPPER.registerModule(simpleModule)
        }
    }
}