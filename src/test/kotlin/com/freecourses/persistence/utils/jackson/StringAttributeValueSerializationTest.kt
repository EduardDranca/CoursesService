package com.freecourses.persistence.utils.jackson

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

class StringAttributeValueSerializationTest {

    companion object {
        private val OBJECT_MAPPER = ObjectMapper();
        @BeforeAll
        @JvmStatic
        fun setup() {
            val simpleModule = SimpleModule()
            val attributeValueSerializer = StringAttributeValueSerializer()
            val attributeValueDeserializer = StringAttributeValueDeserializer()
            simpleModule.addSerializer(AttributeValue::class.java, attributeValueSerializer)
            simpleModule.addDeserializer(AttributeValue::class.java, attributeValueDeserializer)
            OBJECT_MAPPER.registerModule(simpleModule)
        }
    }

    @Test
    fun Given_NullAttributeValue_When_Serializing_Then_ExpectNullString() {
        val result = OBJECT_MAPPER.writeValueAsString(null as AttributeValue?)
        Assertions.assertEquals("null", result)
    }

    @Test
    fun Given_NonStringAttributeValue_When_Serializing_Then_ExpectException() {
        val exception = Assertions.assertThrows(JsonMappingException::class.java) {
            OBJECT_MAPPER.writeValueAsString(AttributeValue.fromN("1"))
        }
        Assertions.assertNotNull(exception.cause)
        Assertions.assertEquals("StringAttributeValueSerializer can only serialize AttributeValues of String type.", exception.cause?.message)
    }

    @Test
    fun Given_StringAttributeValue_When_Serializing_Then_ExpectCorrectResult() {
        val stringAttributeValue = AttributeValue.fromS("test")
        val result = OBJECT_MAPPER.writeValueAsString(stringAttributeValue)
        Assertions.assertEquals("\"test\"", result)
    }

    @Test
    fun Given_NonStringAttributeValue_When_Deserializing_Then_ExpectException() {
        Assertions.assertThrows(JsonMappingException::class.java) {
            OBJECT_MAPPER.readValue("{\"test\": \"test\"}", AttributeValue::class.java)
        }
    }

    @Test
    fun Given_JsonString_When_Deserializing_Then_ExpectCorrectResult() {
        val jsonString = "\"test\""
        val result = OBJECT_MAPPER.readValue(jsonString, AttributeValue::class.java)
        Assertions.assertEquals("test", result.s())
    }
}