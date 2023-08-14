package com.freecourses.service;

import com.freecourses.model.CourseDifficulty;
import com.freecourses.model.ListCoursesResponse;

import javax.validation.constraints.NotNull;
import java.nio.ByteBuffer;

public interface CoursesService {
    ListCoursesResponse getCourses(@NotNull(message = "category can not be null") String category,
                                   String subcategory, CourseDifficulty difficulty, ByteBuffer lastPageToken);
}
