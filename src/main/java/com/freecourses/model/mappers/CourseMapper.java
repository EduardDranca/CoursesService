package com.freecourses.model.mappers;

import com.freecourses.model.Course;
import com.freecourses.model.CreateCourseRequest;
import com.freecourses.model.ListCoursesResponse;
import com.freecourses.persistence.model.CourseDO;
import com.freecourses.persistence.utils.PageTokenConverter;
import com.freecourses.persistence.utils.PageTokenConverterImpl;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;

import java.util.List;

@Mapper
public abstract class CourseMapper {
    protected static final PageTokenConverter PAGE_TOKEN_CONVERTER = new PageTokenConverterImpl();
    public static CourseMapper INSTANCE = Mappers.getMapper(CourseMapper.class);

    public abstract Course toCourse(CourseDO courseDO);
    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
    @Mapping(target = "sortKey", source = "category")
    public abstract CourseDO toCourseDO(CreateCourseRequest createCourseRequest);
    public abstract List<Course> toCourseList(List<CourseDO> courseDOList);
    @AfterMapping
    void setCourseGsiSK(@MappingTarget CourseDO courseDO) {
        courseDO.setCsGsiSk(String.format("%s#%s", courseDO.getDifficulty(), courseDO.getId()));
    }

    @Mapping(target = "courses", expression = "java(toCourseList(coursesPage.items()))")
    @Mapping(target = "nextPageToken", expression = "java(PAGE_TOKEN_CONVERTER.serialize(coursesPage.lastEvaluatedKey()))")
    public abstract ListCoursesResponse toListCoursesResponse(Page<CourseDO> coursesPage);
}
