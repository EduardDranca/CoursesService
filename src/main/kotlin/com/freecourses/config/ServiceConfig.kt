package com.freecourses.config

import com.freecourses.model.mappers.CourseMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class ServiceConfig {
    @Bean
    open fun courseMapper(): CourseMapper {
        return CourseMapper.INSTANCE
    }
}