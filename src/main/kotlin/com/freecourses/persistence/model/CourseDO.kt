package com.freecourses.persistence.model

import com.freecourses.model.CourseDifficulty
import com.freecourses.model.CourseSource
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey
import java.net.URI
import java.util.*

@DynamoDbBean
data class CourseDO(
    @get:DynamoDbPartitionKey
    var id: UUID? = null,
    var category: String? = null,
    @get:DynamoDbSecondaryPartitionKey(indexNames = [INDEX_NAME])
    var partitionKey: String? = null,
    var description: String? = null,
    var uri: URI? = null,
    var subcategories: List<String> = ArrayList<String>(),
    var source: CourseSource? = null,
    var difficulty: CourseDifficulty? = null
) {
    init {
        this.partitionKey = Optional.ofNullable(partitionKey).orElse(category)
    }

    @get:DynamoDbSecondarySortKey(indexNames = [INDEX_NAME])
    var csGsiSk: String? = null

    companion object {
        const val INDEX_NAME = "category-subcategory-index"
    }
}