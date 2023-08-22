package com.freecourses.service

import com.freecourses.model.Course
import com.freecourses.model.CourseDifficulty
import com.freecourses.model.CreateCourseRequest
import com.freecourses.model.ListCoursesResponse
import com.freecourses.model.mappers.CourseMapper
import com.freecourses.persistence.CoursesRepository
import com.freecourses.persistence.utils.PageTokenConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.util.*

@Service
class CoursesServiceImpl(@Autowired private val coursesRepository: CoursesRepository,
                         @Autowired private val pageTokenConverter: PageTokenConverter): CoursesService {
    val courseMapper = CourseMapper.INSTANCE

    override fun getCourses(
        category: String,
        subcategory: String?,
        difficulty: CourseDifficulty?,
        pageSize: Int,
        nextPageToken: ByteArray?
    ): ListCoursesResponse {
        val lastPageKey: Map<String, AttributeValue>? = pageTokenConverter.deserialize(nextPageToken)
        if (subcategory == null) {
            return courseMapper.toListCoursesResponse(coursesRepository.getCourses(category, pageSize, lastPageKey))
        }
        if (difficulty == null) {
            return courseMapper.toListCoursesResponse(coursesRepository.getCourses(category, subcategory, pageSize, lastPageKey))
        }
        return courseMapper.toListCoursesResponse(coursesRepository.getCourses(category, subcategory, difficulty, pageSize, lastPageKey))
    }

    override fun getCourse(courseId: UUID): Course {
        return courseMapper.toCourse(coursesRepository.getCourse(courseId))
    }

    override fun createCourse(course: CreateCourseRequest): Course {
        return courseMapper.toCourse(coursesRepository.createCourse(courseMapper.toCourseDO(course)))
    }
}