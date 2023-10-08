package com.freecourses.controller

import com.freecourses.model.CourseNotFoundError
import com.freecourses.model.InvalidInputError
import com.freecourses.model.exceptions.CourseNotFoundException
import org.springframework.beans.TypeMismatchException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class CoursesApplicationExceptionHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler(CourseNotFoundException::class)
    fun handleCourseNotFoundException(e: CourseNotFoundException): ResponseEntity<CourseNotFoundError> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(CourseNotFoundError()
                .courseId(e.courseId)
                .message(e.message))
    }

    override fun handleTypeMismatch(
        ex: TypeMismatchException, headers: HttpHeaders, status: HttpStatus, request: WebRequest
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(InvalidInputError().message(ex.cause!!.message))
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException, headers: HttpHeaders, status: HttpStatus, request: WebRequest
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(InvalidInputError().message(ex.cause!!.message))
    }
}