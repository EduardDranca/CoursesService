package com.freecourses.persistence.utils

import software.amazon.awssdk.services.dynamodb.model.AttributeValue

interface PageTokenConverter {
    fun serialize(lastEvaluatedKey: Map<String, AttributeValue>?): String?
    fun deserialize(lastPageToken: ByteArray?): Map<String, AttributeValue>?
}