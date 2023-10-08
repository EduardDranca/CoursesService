package com.freecourses.model.exceptions

class CourseServiceException(message: String, cause: Throwable?) : RuntimeException(message, cause)