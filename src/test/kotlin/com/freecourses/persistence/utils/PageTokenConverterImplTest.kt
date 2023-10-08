package com.freecourses.persistence.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.MapType
import com.fasterxml.jackson.databind.type.TypeFactory
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

class PageTokenConverterImplTest {

    companion object {
        private val OBJECT_MAPPER = mockk<ObjectMapper>()
        private val TYPE_FACTORY = mockk<TypeFactory>()
        private val MAP_TYPE = TypeFactory.defaultInstance().constructMapType(Map::class.java, String::class.java, AttributeValue::class.java)
        private val BYTE_ARRAY = ByteArray(8) { it.toByte() }
        private val LAST_EVALUATED_KEY = mapOf(Pair("abc", AttributeValue.fromS("xyz")))

        @JvmStatic
        @BeforeAll
        fun setup() {
            every { OBJECT_MAPPER.writeValueAsBytes(any()) } returns BYTE_ARRAY
            every { OBJECT_MAPPER.readValue(any<ByteArray>(), any<MapType>()) as Map<String, AttributeValue> } returns LAST_EVALUATED_KEY
            every { TYPE_FACTORY.constructMapType(Map::class.java, String::class.java, AttributeValue::class.java) } returns MAP_TYPE
            every { OBJECT_MAPPER.typeFactory } returns TYPE_FACTORY
        }
    }

    private val unit = PageTokenConverterImpl(OBJECT_MAPPER)

    @Test
    fun Given_NullLastEvaluatedKeyMap_When_Serialize_Then_ReturnNull() {
        val serialized = unit.serialize(null)
        Assertions.assertNull(serialized)
    }

    @Test
    fun Given_LastEvaluatedKeyMap_When_Serialize_Then_ReturnByteArray() {
        val serialized = unit.serialize(LAST_EVALUATED_KEY)
        Assertions.assertNotNull(serialized)
        Assertions.assertSame(serialized, BYTE_ARRAY)
        verify { OBJECT_MAPPER.writeValueAsBytes(LAST_EVALUATED_KEY) }
        confirmVerified(OBJECT_MAPPER)
    }

    @Test
    fun Given_NullByteArray_When_Deserialize_Then_ReturnNull() {
        val deserialized = unit.deserialize(null)
        Assertions.assertNull(deserialized)
    }

    @Test
    fun Given_ByteArray_When_Deserialize_Then_ReturnLastEvaluatedKeyMap() {
        val deserialized = unit.deserialize(BYTE_ARRAY)
        Assertions.assertNotNull(deserialized)
        Assertions.assertSame(deserialized, LAST_EVALUATED_KEY)
        verify { OBJECT_MAPPER.typeFactory }
        verify { OBJECT_MAPPER.readValue(BYTE_ARRAY, MAP_TYPE) }
        confirmVerified(OBJECT_MAPPER)
    }
}