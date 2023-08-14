package com.freecourses.model.exceptions

import java.util.*

class CourseNotFoundException(val courseId: UUID, message: String?) : RuntimeException(message)