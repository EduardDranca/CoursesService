
import com.freecourses.persistence.CoursesRepository
import com.freecourses.persistence.model.CourseDO
import io.mockk.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import software.amazon.awssdk.core.pagination.sync.SdkIterable
import software.amazon.awssdk.enhanced.dynamodb.*
import software.amazon.awssdk.enhanced.dynamodb.model.Page
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.TransactWriteItemsEnhancedRequest
import java.util.*

class CoursesRepositoryTest {
    companion object {
        private val coursesTable = mockk<DynamoDbTable<CourseDO>>()
        private val dynamoDbClient = mockk<DynamoDbEnhancedClient>()
        private val coursesTableIndex = mockk<DynamoDbIndex<CourseDO>>()
        private val emptyIterable = object : SdkIterable<Page<CourseDO>> {
            override fun iterator(): MutableIterator<Page<CourseDO>> {
                return Collections.emptyIterator()
            }
        }
        private var coursesRepository: CoursesRepository

        init {
            every { coursesTable.index(CourseDO.INDEX_NAME) } returns coursesTableIndex
            coursesRepository = CoursesRepository(coursesTable, dynamoDbClient)
        }
    }

    @BeforeEach
    fun setUp() {
        clearMocks(coursesTable)
        every { coursesTable.tableSchema() } returns TableSchema.fromBean(CourseDO::class.java)
        every { coursesTable.tableName() } returns "courses"
        every { coursesTable.mapperExtension() } returns object: DynamoDbEnhancedClientExtension {}
        every { dynamoDbClient.transactWriteItems(any<TransactWriteItemsEnhancedRequest>()) } returns null
    }

    @Test
    fun Given_ValidCourse_When_CreateCourse_Then_ReturnCourse() {
        println("${CourseDO::class.java}#${null}")
        val course = CourseDO()
        val transactionRequest = TransactWriteItemsEnhancedRequest.builder()
            .addPutItem(coursesTable, course)
            .build()
        val resultCourse = coursesRepository.createCourse(course)
        Assertions.assertSame(resultCourse, course)
        verify { dynamoDbClient.transactWriteItems(eq(transactionRequest)) }
        confirmVerified(dynamoDbClient)
    }

    @Test
    fun Given_CourseWithSubcategories_When_CreateCourse_Then_WriteMultipleItems() {
        val course = CourseDO(subcategories = listOf("sub1", "sub2"), category = "category")
        val transactionRequest = TransactWriteItemsEnhancedRequest.builder()
            .addPutItem(coursesTable, course)
            .addPutItem(
                coursesTable, course.copy(
                    sortKey = "${course.category}#sub1"
                )
            )
            .addPutItem(
                coursesTable, course.copy(
                    sortKey = "${course.category}#sub2"
                )
            )
            .build()
        val resultCourse = coursesRepository.createCourse(course)
        Assertions.assertSame(resultCourse, course)
        verify { dynamoDbClient.transactWriteItems(eq(transactionRequest)) }
        confirmVerified(dynamoDbClient)
    }

    @Test
    fun Given_NonxistentCourse_When_GetCourse_Then_ReturnNull() {
        val course = CourseDO(id = UUID.randomUUID())
        every { coursesTable.getItem(eq(course)) } returns null
        val resultCourse = coursesRepository.getCourse(course.id!!)
        Assertions.assertNull(resultCourse)
        verify { coursesTable.getItem(eq(course)) }
        confirmVerified(coursesTable)
    }

    @Test
    fun Given_ExistentCourse_When_GetCourse_Then_ReturnCourse() {
        val course = CourseDO(id = UUID.randomUUID())
        every { coursesTable.getItem(eq(course)) } returns course
        val resultCourse = coursesRepository.getCourse(course.id!!)
        Assertions.assertSame(resultCourse, course)
        verify { coursesTable.getItem(eq(course)) }
        confirmVerified(coursesTable)
    }

    @Test
    fun Given_NoCourses_When_GetCoursesByCategory_Then_ReturnEmptyPage() {
        every { coursesTableIndex.query(any() as QueryEnhancedRequest) } returns emptyIterable
        val resultPage = coursesRepository.getCourses("category", 10, mapOf())
        Assertions.assertEquals(0, resultPage.items().size)
        verify { coursesTableIndex.query(QueryEnhancedRequest.builder()
            .limit(10)
            .exclusiveStartKey(mapOf())
            .queryConditional(QueryConditional.keyEqualTo { it.partitionValue("category") })
            .build())}
        confirmVerified(coursesTableIndex)
    }
}
