package com.freecourses.controller

import com.freecourses.model.Course
import com.freecourses.model.CourseDifficulty
import com.freecourses.model.CreateCourseRequest
import com.freecourses.model.mappers.CourseMapper
import com.freecourses.persistence.CoursesRepository
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/v1/courses")
class CoursesController(private val coursesRepository: CoursesRepository) {
    private val courseMapper = CourseMapper.INSTANCE

    @CrossOrigin
    @GetMapping(path = ["/{courseId}"], produces = ["application/json"])
    fun getCourse(@Validated @PathVariable courseId: UUID): ResponseEntity<Course> {
        return ResponseEntity.ok(courseMapper.toCourse(
            coursesRepository.getCourse(courseId)
        ));
    }

    @CrossOrigin
    @GetMapping(produces = ["application/json"])
    fun getCourses(
        @RequestParam category: String,
        @RequestParam(required = false) subcategory: String?,
        @RequestParam(required = false) difficulty: CourseDifficulty?
    ): ResponseEntity<List<Course>> {
        if (subcategory == null) {
            return ResponseEntity.ok(courseMapper.toCourseList(
                coursesRepository.getCourses(category)
            ));
        }
        if (difficulty == null) {
            return ResponseEntity.ok(courseMapper.toCourseList(
                    coursesRepository.getCourses(category, subcategory)
            ));
        }
        return ResponseEntity.ok(
            courseMapper.toCourseList(coursesRepository.getCourses(
                    category, subcategory, difficulty)
            ));
    }

    @CrossOrigin
    @PostMapping(produces = ["application/json"])
    fun createCourse(@Validated @RequestBody createCourseRequest: CreateCourseRequest): ResponseEntity<Course> {
        val course = courseMapper.toCourseDO(createCourseRequest)
        coursesRepository.createCourse(course)
        return ResponseEntity.ok(courseMapper.toCourse(course))
    }
}