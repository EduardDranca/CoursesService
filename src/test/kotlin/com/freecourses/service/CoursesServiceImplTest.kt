package com.freecourses.service

import com.freecourses.model.Course
import com.freecourses.model.CourseDifficulty
import com.freecourses.model.CreateCourseRequest
import com.freecourses.model.ListCoursesResponse
import com.freecourses.model.exceptions.CourseNotFoundException
import com.freecourses.model.exceptions.CourseServiceException
import com.freecourses.model.mappers.CourseMapper
import com.freecourses.persistence.CoursesRepository
import com.freecourses.persistence.model.CourseDO
import com.freecourses.persistence.utils.PageTokenConverter
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import software.amazon.awssdk.enhanced.dynamodb.model.Page
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.util.*

class CoursesServiceImplTest {
    private val pageTokenConverter = mockk<PageTokenConverter>()
    private val coursesRepository = mockk<CoursesRepository>()
    private val coursesMapper = mockk<CourseMapper>()
    private val coursesService = CoursesServiceImpl(coursesRepository, pageTokenConverter, coursesMapper)
    private val courseDO = CourseDO()
    private val course = Course()
    private val createCourseRequest = CreateCourseRequest()
    private val lastPageKey = mapOf(Pair("abc", AttributeValue.fromS("abc")))
    private val listCoursesResponse = ListCoursesResponse(listOf(course))

    @BeforeEach
    fun setUp() {
        every { coursesMapper.toCourse(any<CourseDO>()) } returns course
        every { coursesMapper.toCourseDO(any<CreateCourseRequest>()) }  returns courseDO
        every { coursesMapper.toListCoursesResponse(any<Page<CourseDO>>()) } returns listCoursesResponse
        every { pageTokenConverter.deserialize(any()) } returns lastPageKey
        every { pageTokenConverter.deserialize(null) } returns null
    }

    @Test
    fun Given_NullCourse_When_GetCourseById_Then_ThrowCourseNotFoundException() {
        val courseId = UUID.randomUUID()
        val courseNotFoundException = CourseNotFoundException(courseId, "The course with id: <$courseId> could not be found.")
        every { coursesRepository.getCourse(any<UUID>()) } returns null
        val thrown = Assertions.assertThrows(CourseNotFoundException::class.java) {
            coursesService.getCourse(courseId)
        }
        Assertions.assertEquals(courseNotFoundException, thrown)
        verify { coursesRepository.getCourse(eq(courseId)) }
        confirmVerified(coursesRepository)
    }

    @Test
    fun Given_RepositoryException_When_GetCourseById_Then_ThrowException() {
        val courseId = UUID.randomUUID()
        val repositoryException = Exception("Repository exception")
        every { coursesRepository.getCourse(any<UUID>()) } throws repositoryException
        val thrown = Assertions.assertThrows(CourseServiceException::class.java) {
            coursesService.getCourse(courseId)
        }
        Assertions.assertEquals("Error while getting course with id <$courseId>.", thrown.message)
        Assertions.assertSame(repositoryException, thrown.cause)
        verify { coursesRepository.getCourse(eq(courseId)) }
        confirmVerified(coursesRepository)
    }

    @Test
    fun Given_ExistentCourseId_When_GetCourseById_Then_ReturnCourse() {
        every { coursesRepository.getCourse(any<UUID>()) } returns courseDO
        val courseId = UUID.randomUUID()
        val returnedCourse = coursesService.getCourse(courseId)
        Assertions.assertSame(course, returnedCourse)
        verify { coursesRepository.getCourse(eq(courseId)) }
        confirmVerified(coursesRepository)
    }

    @Test
    fun Given_RepositoryException_When_CreateCourse_Then_ThrowException() {
        val repositoryException = Exception("Repository exception")
        every { coursesRepository.createCourse(any<CourseDO>()) } throws repositoryException
        val thrown = Assertions.assertThrows(CourseServiceException::class.java) {
            coursesService.createCourse(createCourseRequest)
        }
        Assertions.assertEquals("Error while creating course.", thrown.message)
        Assertions.assertSame(repositoryException, thrown.cause)
        verify { coursesRepository.createCourse(eq(courseDO)) }
        confirmVerified(coursesRepository)
    }

    @Test
    fun Given_ValidCreateCourseRequest_When_CreateCourse_Then_ReturnCourse() {
        every { coursesRepository.createCourse(any<CourseDO>()) } returns courseDO
        val returnedCourse = coursesService.createCourse(createCourseRequest)
        Assertions.assertSame(course, returnedCourse)
        verify { coursesRepository.createCourse(eq(courseDO)) }
        confirmVerified(coursesRepository)
    }

    @Test
    fun Given_RepositoryException_When_GetCourses_Then_ThrowException() {
        val category = "Programming"
        val pageSize = 10
        val repositoryException = Exception("Repository exception")
        every { coursesRepository.getCourses(any(), any(), any()) } throws repositoryException
        val thrown = Assertions.assertThrows(CourseServiceException::class.java) {
            coursesService.getCourses(category, null, null, pageSize, null)
        }
        Assertions.assertEquals("Error while getting courses.", thrown.message)
        Assertions.assertSame(repositoryException, thrown.cause)
        verify { coursesRepository.getCourses(eq(category), eq(pageSize), null) }
        verify { pageTokenConverter.deserialize(null) }
        confirmVerified(coursesRepository)
        confirmVerified(pageTokenConverter)
    }

    @Test
    fun Given_Category_When_GetCourses_Then_ReturnCourses() {
        val category = "Programming"
        val pageSize = 10
        val page = Page.create(listOf(courseDO))
        every { coursesRepository.getCourses(any(), any(), any()) } returns page
        val courses = coursesService.getCourses(category, null, null, pageSize, null)
        Assertions.assertSame(listCoursesResponse, courses)
        verify { coursesRepository.getCourses(eq(category), eq(pageSize), null) }
        verify { pageTokenConverter.deserialize(null) }
        verify { coursesMapper.toListCoursesResponse(eq(page)) }
        confirmVerified(coursesRepository)
        confirmVerified(pageTokenConverter)
        confirmVerified(coursesMapper)
    }

    @Test
    fun Given_Subcategory_When_GetCourses_Then_ReturnCourses() {
        val category = "Programming"
        val subcategory = "Java"
        val pageSize = 10
        val page = Page.create(listOf(courseDO))
        every { coursesRepository.getCourses(any(), any(), any(), any()) } returns page
        val courses = coursesService.getCourses(category, subcategory, null, pageSize, null)
        Assertions.assertSame(listCoursesResponse, courses)
        verify { coursesRepository.getCourses(eq(category), eq(subcategory), eq(pageSize), null) }
        verify { pageTokenConverter.deserialize(null) }
        verify { coursesMapper.toListCoursesResponse(eq(page)) }
        confirmVerified(coursesRepository)
        confirmVerified(pageTokenConverter)
        confirmVerified(coursesMapper)
    }

    @Test
    fun Given_SubcategoryAndDifficulty_When_GetCourses_Then_ReturnCourses() {
        val category = "Programming"
        val subcategory = "Java"
        val difficulty = CourseDifficulty.ADVANCED
        val pageSize = 10
        val page = Page.create(listOf(courseDO))
        every { coursesRepository.getCourses(any(), any(), any(), any(), any()) } returns page
        val courses = coursesService.getCourses(category, subcategory, difficulty, pageSize, null)
        Assertions.assertSame(listCoursesResponse, courses)
        verify { coursesRepository.getCourses(eq(category), eq(subcategory), eq(difficulty), eq(pageSize), null) }
        verify { pageTokenConverter.deserialize(null) }
        verify { coursesMapper.toListCoursesResponse(eq(page)) }
        confirmVerified(pageTokenConverter)
        confirmVerified(coursesRepository)
        confirmVerified(coursesMapper)
    }

    @Test
    fun Given_PageToken_When_GetCourses_Then_ReturnCourses() {
        val category = "Programming"
        val pageSize = 10
        val pageToken = "abc".toByteArray()
        val page = Page.create(listOf(courseDO))
        every { coursesRepository.getCourses(any(), any(), any()) } returns page
        val courses = coursesService.getCourses(category, null, null, pageSize, pageToken)
        Assertions.assertSame(listCoursesResponse, courses)
        verify { coursesRepository.getCourses(eq(category), eq(pageSize), eq(lastPageKey)) }
        verify { pageTokenConverter.deserialize(eq(pageToken)) }
        verify { coursesMapper.toListCoursesResponse(eq(page)) }
        confirmVerified(coursesRepository)
        confirmVerified(pageTokenConverter)
        confirmVerified(coursesMapper)
    }
}