package com.freecourses.service

import com.freecourses.model.Course
import com.freecourses.model.CourseDifficulty
import com.freecourses.model.CreateCourseRequest
import com.freecourses.model.ListCoursesResponse
import java.util.*

interface CoursesService {
    fun getCourses(
        category: String, subcategory: String?, difficulty: CourseDifficulty?, pageSize: Int, nextPageToken: ByteArray?
    ): ListCoursesResponse

    fun getCourse(courseId: UUID): Course

    fun createCourse(course: CreateCourseRequest): Course
}