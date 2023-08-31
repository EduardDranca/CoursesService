package com.freecourses.persistence.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.MapType
import com.fasterxml.jackson.databind.type.TypeFactory
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
class PageTokenConverterImplTest {

    companion object {
        private val OBJECT_MAPPER = mock(ObjectMapper::class.java)
        private val TYPE_FACTORY = mock(TypeFactory::class.java)
        private val MAP_TYPE = TypeFactory.defaultInstance().constructMapType(Map::class.java, String::class.java, AttributeValue::class.java)
        private val BYTE_ARRAY = ByteArray(8, { it.toByte() })
        private val LAST_EVALUATED_KEY = mapOf(Pair("abc", AttributeValue.fromS("xyz")))

        @JvmStatic
        @BeforeAll
        fun setup() {
            `when`(OBJECT_MAPPER.writeValueAsBytes(any())).thenReturn(BYTE_ARRAY)
            `when`(OBJECT_MAPPER.readValue(isA(ByteArray::class.java), isA(MapType::class.java)) as Map<String, AttributeValue>?).thenReturn(LAST_EVALUATED_KEY)
            `when`(TYPE_FACTORY.constructMapType(eq(Map::class.java), eq(String::class.java), eq(AttributeValue::class.java))).thenReturn(MAP_TYPE)
            `when`(OBJECT_MAPPER.typeFactory).thenReturn(TYPE_FACTORY)
        }
    }

    @AfterEach
    fun tearDown() {
        clearInvocations(OBJECT_MAPPER)
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
        verify(OBJECT_MAPPER).writeValueAsBytes(LAST_EVALUATED_KEY)
        verifyNoMoreInteractions(OBJECT_MAPPER)
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
        verify(OBJECT_MAPPER).typeFactory
        verify(OBJECT_MAPPER).readValue(BYTE_ARRAY, MAP_TYPE) as Map<String, AttributeValue>?
        verifyNoMoreInteractions(OBJECT_MAPPER)
    }
}