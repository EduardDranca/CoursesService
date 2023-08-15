package com.freecourses.persistence

import com.freecourses.model.CourseDifficulty
import com.freecourses.model.exceptions.CourseNotFoundException
import com.freecourses.persistence.model.CourseDO
import org.springframework.stereotype.Repository
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.Expression
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.TransactWriteItemsEnhancedRequest
import java.util.*
import java.util.stream.Collectors

@Repository
class CoursesRepository(private val coursesTable: DynamoDbTable<CourseDO>, private val dynamoDbClient: DynamoDbEnhancedClient) {
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

    fun getCourses(category: String): List<CourseDO> {
        val courseCategoryIndex = coursesTable.index(CourseDO.INDEX_NAME)
        val queryConditional = QueryConditional.keyEqualTo(Key.builder().partitionValue(category).build())
        val query = QueryEnhancedRequest.builder()
            .queryConditional(queryConditional)
            .limit(2)
            .build()
        return courseCategoryIndex.query(query)
            .stream()
            .flatMap { it.items().stream() }
            .collect(Collectors.toList())
    }

    fun getCourses(category: String, subcategory: String): List<CourseDO> {
        val courseCategoryIndex = coursesTable.index(CourseDO.INDEX_NAME)
        val queryConditional = QueryConditional.keyEqualTo { b -> b.partitionValue("$category#$subcategory") }
        val query = QueryEnhancedRequest.builder()
            .queryConditional(queryConditional)
            .build()
        return courseCategoryIndex.query(query)
            .stream()
            .flatMap { it.items().stream() }
            .collect(Collectors.toList())
    }

    fun getCourses(category: String, subcategory: String, difficulty: CourseDifficulty): List<CourseDO> {
        val courseCategoryIndex = coursesTable.index(CourseDO.INDEX_NAME)
        val queryConditional = QueryConditional.keyEqualTo { b -> b.sortValue("$category#$subcategory") }
        val beginsWithFilterExpression = Expression.builder()
            .expression("begins_with(csGsiSk, $difficulty#)")
            .build()
        val query = QueryEnhancedRequest.builder()
            .queryConditional(queryConditional)
            .filterExpression(beginsWithFilterExpression)
            .build()
        return courseCategoryIndex.query(query)
            .stream()
            .flatMap { it.items().stream() }
            .collect(Collectors.toList())
    }
}