package com.freecourses.controller

import com.freecourses.api.CoursesApi
import com.freecourses.model.Course
import com.freecourses.model.CourseDifficulty
import com.freecourses.model.CreateCourseRequest
import com.freecourses.model.ListCoursesResponse
import com.freecourses.service.CoursesService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URLDecoder
import java.util.*

@RestController
@RequestMapping("/v1")
class CoursesController(@Autowired private val coursesService: CoursesService): CoursesApi {
    override fun createCourse(createCourseRequest: CreateCourseRequest): ResponseEntity<Course> {
        return ResponseEntity.ok(coursesService.createCourse(createCourseRequest))
    }

    override fun getCourseById(courseId: UUID): ResponseEntity<Course> {
        return ResponseEntity.ok(coursesService.getCourse(courseId))
    }

    override fun listCourses(
        category: String,
        subcategory: String?,
        maxCourses: Int?,
        difficulty: CourseDifficulty?,
        nextPageToken: String?
    ): ResponseEntity<ListCoursesResponse> {
        val pageSize: Int = maxCourses ?: 50
        val nextPageTokenBytes = nextPageToken?.let { Base64.getDecoder().decode(URLDecoder.decode(it, Charsets.UTF_8)) }
        return ResponseEntity.ok(coursesService.getCourses(category, subcategory, difficulty, pageSize, nextPageTokenBytes))
    }
}