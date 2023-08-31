package com.freecourses.persistence.model

import com.freecourses.model.CourseDifficulty
import com.freecourses.model.CourseSource
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*
import java.net.URI
import java.util.*

@DynamoDbBean
data class CourseDO(
    @get:DynamoDbPartitionKey
    var id: UUID? = null,
    var category: String? = null,
    @get:DynamoDbSortKey
    @get:DynamoDbSecondaryPartitionKey(indexNames = [INDEX_NAME])
    var sortKey: String? = null,
    var description: String? = null,
    var uri: URI? = null,
    var subcategories: List<String> = ArrayList<String>(),
    var source: CourseSource? = null,
    var difficulty: CourseDifficulty? = null
) {
    init {
        this.sortKey = Optional.ofNullable(sortKey).orElse(category)
    }

    @get:DynamoDbSecondarySortKey(indexNames = [INDEX_NAME])
    var csGsiSk: String? = null

    companion object {
        const val INDEX_NAME = "category-subcategory-index"
    }
}