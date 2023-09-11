package com.freecourses.model.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.freecourses.model.Course;
import com.freecourses.model.CreateCourseRequest;
import com.freecourses.model.ListCoursesResponse;
import com.freecourses.persistence.model.CourseDO;
import com.freecourses.persistence.utils.PageTokenConverter;
import com.freecourses.persistence.utils.PageTokenConverterImpl;
import com.freecourses.persistence.utils.jackson.StringAttributeValueDeserializer;
import com.freecourses.persistence.utils.jackson.StringAttributeValueSerializer;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Mapper
public abstract class CourseMapper {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    protected static final PageTokenConverter PAGE_TOKEN_CONVERTER = new PageTokenConverterImpl(OBJECT_MAPPER);
    public static CourseMapper INSTANCE = Mappers.getMapper(CourseMapper.class);

    static {
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(AttributeValue.class, new StringAttributeValueSerializer());
        simpleModule.addDeserializer(AttributeValue.class, new StringAttributeValueDeserializer());
        OBJECT_MAPPER.registerModule(simpleModule);
    }

    public abstract Course toCourse(CourseDO courseDO);
    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
    @Mapping(target = "partitionKey", source = "category")
    public abstract CourseDO toCourseDO(CreateCourseRequest createCourseRequest);
    public abstract List<Course> toCourseList(List<CourseDO> courseDOList);
    @AfterMapping
    void setCourseGsiSK(@MappingTarget CourseDO courseDO) {
        courseDO.setCsGsiSk(String.format("%s#%s", courseDO.getDifficulty(), courseDO.getId()));
    }

    @Mapping(target = "courses", expression = "java(toCourseList(coursesPage.items()))")
    @Mapping(target = "nextPageToken", expression = "java(serializeLastEvaluatedKey(coursesPage.lastEvaluatedKey()))")
    public abstract ListCoursesResponse toListCoursesResponse(Page<CourseDO> coursesPage);

    protected static String serializeLastEvaluatedKey(Map<String, AttributeValue> lastEvaluatedKey) {
        return URLEncoder.encode(Base64.getEncoder().encodeToString(PAGE_TOKEN_CONVERTER.serialize(lastEvaluatedKey)), StandardCharsets.UTF_8);
    }
}
