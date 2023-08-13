package com.freecourses.model.mappers;

import com.freecourses.model.Course;
import com.freecourses.model.CreateCourseRequest;
import com.freecourses.persistence.model.CourseDO;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CourseMapper {
    CourseMapper INSTANCE = Mappers.getMapper(CourseMapper.class);

    Course toCourse(CourseDO courseDO);
    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
    @Mapping(target = "sortKey", source = "category")
    CourseDO toCourseDO(CreateCourseRequest createCourseRequest);
    List<Course> toCourseList(List<CourseDO> courseDOList);
    @AfterMapping
    default void setCourseGsiSK(@MappingTarget CourseDO courseDO) {
        courseDO.setCsGsiSk(String.format("%s#%s", courseDO.getDifficulty(), courseDO.getId()));
    }
}
