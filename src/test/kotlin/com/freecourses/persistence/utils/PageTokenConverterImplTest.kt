package com.freecourses.persistence.utils

import org.junit.jupiter.api.Test
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

class PageTokenConverterImplTest {
    @Test
    fun test() {
        val converter = PageTokenConverterImpl()
        val serialized = converter.serialize(mapOf(Pair("abc", AttributeValue.fromS("xyz"))))
        val deserialized = converter.deserialize(serialized)
    }
}