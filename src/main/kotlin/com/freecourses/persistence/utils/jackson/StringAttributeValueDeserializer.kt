package com.freecourses.persistence.utils.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

class StringAttributeValueDeserializer: StdDeserializer<AttributeValue> {
    constructor(): this(null)
    constructor(clazz: Class<AttributeValue>?): super(clazz)

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): AttributeValue {
        return AttributeValue.fromS(p.codec.readValue(p, String::class.java))
    }


}