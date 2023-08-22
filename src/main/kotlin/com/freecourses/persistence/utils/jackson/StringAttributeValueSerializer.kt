package com.freecourses.persistence.utils.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

class StringAttributeValueSerializer: StdSerializer<AttributeValue> {
    constructor(): this(null)
    constructor(clazz: Class<AttributeValue>?): super(clazz)
    override fun serialize(value: AttributeValue?, gen: JsonGenerator, provider: SerializerProvider?) {
        if (value == null) {
            return gen.writeNull()
        }

        if (value.type() != AttributeValue.Type.S) {
            throw RuntimeException("StringAttributeValueSerializer can only serialize AttributeValues of String type")
        }

        return gen.writeString(value.s())
    }
}