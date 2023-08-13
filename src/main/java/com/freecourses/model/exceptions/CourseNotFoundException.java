package com.freecourses.model.exceptions;

import java.util.UUID;

public class CourseNotFoundException extends RuntimeException {
    private final UUID courseId;

    public CourseNotFoundException(UUID courseId, String message) {
        super(message);
        this.courseId = courseId;
    }

    public UUID getCourseId() {
        return courseId;
    }
}
