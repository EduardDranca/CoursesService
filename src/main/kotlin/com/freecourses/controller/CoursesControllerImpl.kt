package com.freecourses.controller

import com.freecourses.model.Course
import com.freecourses.model.CourseDifficulty
import com.freecourses.model.CreateCourseRequest
import com.freecourses.model.ListCoursesResponse
import com.freecourses.service.CoursesService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.net.URLDecoder
import java.util.*

@RestController
@RequestMapping("/v1/courses")
class CoursesControllerImpl(@Autowired private val coursesService: CoursesService): CoursesController {
    @CrossOrigin
    @GetMapping(path = ["/{courseId}"], produces = ["application/json"])
    override fun getCourse(@Validated @PathVariable courseId: UUID): ResponseEntity<Course> {
        return ResponseEntity.ok(coursesService.getCourse(courseId))
    }

    //TODO: only allow crossorigin requests for local dev
    //TODO: add validated
    @CrossOrigin
    @GetMapping(produces = ["application/json"])
    override fun getCourses(
        @RequestParam category: String,
        @RequestParam(required = false) subcategory: String?,
        @RequestParam(required = false) difficulty: CourseDifficulty?,
        @RequestParam(required = false) maxCourses: Int?,
        @RequestParam(required = false) nextPageToken: String?
    ): ResponseEntity<ListCoursesResponse> {
        val pageSize: Int = maxCourses ?: 50
        val nextPageTokenBytes = nextPageToken?.let { Base64.getDecoder().decode(URLDecoder.decode(it, Charsets.UTF_8)) }
        return ResponseEntity.ok(coursesService.getCourses(category, subcategory, difficulty, pageSize, nextPageTokenBytes))
    }

    @CrossOrigin
    @PostMapping(produces = ["application/json"])
    override fun createCourse(@RequestBody createCourseRequest: CreateCourseRequest): ResponseEntity<Course> {
        return ResponseEntity.ok(coursesService.createCourse(createCourseRequest))
    }
}