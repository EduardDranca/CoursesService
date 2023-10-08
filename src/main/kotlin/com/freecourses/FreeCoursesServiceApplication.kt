package com.freecourses

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class FreeCoursesServiceApplication

fun main(args: Array<String>) {
    runApplication<FreeCoursesServiceApplication>(*args)
}