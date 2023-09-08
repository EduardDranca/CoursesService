package com.freecourses.persistence

import com.freecourses.model.CourseDifficulty
import com.freecourses.persistence.model.CourseDO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.Expression
import software.amazon.awssdk.enhanced.dynamodb.model.Page
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.TransactWriteItemsEnhancedRequest
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.util.*

@Repository
open class CoursesRepository(@Autowired private val coursesTable: DynamoDbTable<CourseDO>,
                             @Autowired private val dynamoDbClient: DynamoDbEnhancedClient) {
    private val categorySubcategoryIndex: DynamoDbIndex<CourseDO> = coursesTable.index(CourseDO.INDEX_NAME)
    //TODO: add a gsi on the course URL to check if it already exists
    fun createCourse(course: CourseDO): CourseDO {
        val writeCoursesRequest = TransactWriteItemsEnhancedRequest.builder()
            .addPutItem(coursesTable, course)
        course.subcategories.forEach {
            val newCourse: CourseDO = course.copy(
                sortKey = "${course.category}#${it}"
            )
            writeCoursesRequest.addPutItem(coursesTable, newCourse)
        }
        dynamoDbClient.transactWriteItems(writeCoursesRequest.build())
        return course
    }

    fun getCourse(uuid: UUID): CourseDO? {
        return coursesTable.getItem(CourseDO(id = uuid))
    }

    fun getCourses(category: String, pageSize: Int, lastEvaluatedKey: Map<String, AttributeValue>?): Page<CourseDO> {
        return getCourses(category, null, null, pageSize, lastEvaluatedKey)
    }

    fun getCourses(category: String, subcategory: String, pageSize: Int, lastEvaluatedKey: Map<String, AttributeValue>?): Page<CourseDO> {
        return getCourses(category, subcategory, null, pageSize, lastEvaluatedKey)
    }

    fun getCourses(category: String, subcategory: String?, difficulty: CourseDifficulty?,
                   pageSize: Int, lastEvaluatedKey: Map<String, AttributeValue>?):  Page<CourseDO> {
        val queryConditional = getQueryConditional(category, subcategory)
        val beginsWithFilterExpression = difficulty?.let {
            Expression.builder()
            .expression("begins_with(csGsiSk, $it#)")
            .build()
        }
        val query = QueryEnhancedRequest.builder()
            .queryConditional(queryConditional)
            .limit(pageSize)
            .filterExpression(beginsWithFilterExpression)
            .exclusiveStartKey(lastEvaluatedKey)
            .build()
        return executeQuery(query)
    }

    private fun executeQuery(query: QueryEnhancedRequest): Page<CourseDO> {
        return try {
            categorySubcategoryIndex.query(query)
                .first()
        } catch (e: NoSuchElementException) {
            Page.create(Collections.emptyList())
        }
    }

    private fun getQueryConditional(category: String, subcategory: String?): QueryConditional {
        val partitionKeySuffix: String = if (subcategory == null) "" else "#$subcategory"
        return QueryConditional.keyEqualTo { it.partitionValue("$category${partitionKeySuffix}") }
    }
}