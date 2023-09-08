package com.freecourses.service

import com.freecourses.model.Course
import com.freecourses.model.CourseDifficulty
import com.freecourses.model.CreateCourseRequest
import com.freecourses.model.ListCoursesResponse
import com.freecourses.model.exceptions.CourseNotFoundException
import com.freecourses.model.exceptions.CourseServiceException
import com.freecourses.model.mappers.CourseMapper
import com.freecourses.persistence.CoursesRepository
import com.freecourses.persistence.utils.PageTokenConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.util.*

@Service
class CoursesServiceImpl(@Autowired private val coursesRepository: CoursesRepository,
                         @Autowired private val pageTokenConverter: PageTokenConverter,
                         @Autowired private val courseMapper: CourseMapper): CoursesService {
    override fun getCourses(
        category: String,
        subcategory: String?,
        difficulty: CourseDifficulty?,
        pageSize: Int,
        nextPageToken: ByteArray?
    ): ListCoursesResponse {
        try {
            val lastPageKey: Map<String, AttributeValue>? = pageTokenConverter.deserialize(nextPageToken)
            if (subcategory == null) {
                return courseMapper.toListCoursesResponse(coursesRepository.getCourses(category, pageSize, lastPageKey))
            }
            if (difficulty == null) {
                return courseMapper.toListCoursesResponse(coursesRepository.getCourses(category, subcategory, pageSize, lastPageKey))
            }
            return courseMapper.toListCoursesResponse(coursesRepository.getCourses(category, subcategory, difficulty, pageSize, lastPageKey))
        } catch (e: Exception) {
            throw CourseServiceException("Error while getting courses.", e)
        }
    }

    override fun getCourse(courseId: UUID): Course {
        var course: Course? = null
        try {
            course = courseMapper.toCourse(coursesRepository.getCourse(courseId))
        } catch (e: Exception) {
            throw CourseServiceException("Error while getting course with id: <$courseId>.", e)
        }
        return course ?: throw CourseNotFoundException(courseId, "The course with id: <$courseId> could not be found.")
    }

    override fun createCourse(course: CreateCourseRequest): Course {
        //TODO: check if course already exists, insert an entry in the database for the course url in the same transaction as the course creation
        // if that throws an exception from ddb, throw a CourseAlreadyExistsException
        try {
            return courseMapper.toCourse(coursesRepository.createCourse(courseMapper.toCourseDO(course)))
        } catch (e: Exception) {
            throw CourseServiceException("Error while creating course.", e)
        }
    }
}