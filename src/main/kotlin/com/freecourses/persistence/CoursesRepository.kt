package com.freecourses.persistence

import com.freecourses.model.CourseDifficulty
import com.freecourses.model.exceptions.CourseNotFoundException
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
class CoursesRepository(@Autowired private val coursesTable: DynamoDbTable<CourseDO>,
                        @Autowired private val dynamoDbClient: DynamoDbEnhancedClient) {
    private val categorySubcategoryIndex: DynamoDbIndex<CourseDO> = coursesTable.index(CourseDO.INDEX_NAME)
    fun createCourse(course: CourseDO): CourseDO {
        val writeCoursesRequest = TransactWriteItemsEnhancedRequest.builder()
            .addPutItem(coursesTable, course);
        course.subcategories.forEach { subcategory: String ->
            val newCourse: CourseDO = course.copy(
                sortKey = "${course.category}#${subcategory}"
            )
            writeCoursesRequest.addPutItem(coursesTable, newCourse)
        }
        dynamoDbClient.transactWriteItems(writeCoursesRequest.build())
        return course;
    }

    fun getCourse(uuid: UUID): CourseDO {
        return coursesTable.getItem(CourseDO(id = uuid)) ?: throw CourseNotFoundException(
            uuid,
            String.format("The course with id: <%s> could not be found.", uuid)
        )
    }

    fun getCourses(category: String, pageSize: Int, lastEvaluatedKey: Map<String, AttributeValue>?): Page<CourseDO> {
        val queryConditional = getQueryConditional(category)
        val query = QueryEnhancedRequest.builder()
            .queryConditional(queryConditional)
            .limit(pageSize)
            .exclusiveStartKey(lastEvaluatedKey)
            .build()
        return executeQuery(query)
    }

    fun getCourses(category: String, subcategory: String, pageSize: Int, lastEvaluatedKey: Map<String, AttributeValue>?): Page<CourseDO> {
        val queryConditional = getQueryConditional("$category#$subcategory")
        val query = QueryEnhancedRequest.builder()
            .queryConditional(queryConditional)
            .limit(pageSize)
            .exclusiveStartKey(lastEvaluatedKey)
            .build()
        return executeQuery(query)
    }

    fun getCourses(category: String, subcategory: String, difficulty: CourseDifficulty, pageSize: Int, lastEvaluatedKey: Map<String, AttributeValue>?): Page<CourseDO> {
        val queryConditional = getQueryConditional("$category#$subcategory")
        val beginsWithFilterExpression = Expression.builder()
            .expression("begins_with(csGsiSk, $difficulty#)")
            .build()
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

    private fun getQueryConditional(partitionValue: String): QueryConditional {
        return QueryConditional.keyEqualTo {key -> key.partitionValue(partitionValue)}
    }
}