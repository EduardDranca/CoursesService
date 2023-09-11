package com.freecourses.controller

import com.freecourses.model.Course
import com.freecourses.model.CourseDifficulty
import com.freecourses.model.CreateCourseRequest
import com.freecourses.model.ListCoursesResponse
import org.springframework.http.ResponseEntity
import java.util.*

interface CoursesController {
    fun getCourse(courseId: UUID): ResponseEntity<Course>
    fun getCourses(category: String,
                   subcategory: String?,
                   difficulty: CourseDifficulty?,
                   maxCourses: Int?,
                   nextPageToken: String?): ResponseEntity<ListCoursesResponse>
    fun createCourse(createCourseRequest: CreateCourseRequest): ResponseEntity<Course>
}
