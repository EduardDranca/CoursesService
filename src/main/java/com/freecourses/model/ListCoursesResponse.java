package com.freecourses.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.freecourses.model.Course;
import java.util.ArrayList;
import java.util.List;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ListCoursesResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-13T00:16:21.000191921+03:00[Europe/Bucharest]")
public class ListCoursesResponse {

  @Valid
  private List<@Valid Course> courses;

  public ListCoursesResponse courses(List<@Valid Course> courses) {
    this.courses = courses;
    return this;
  }

  public ListCoursesResponse addCoursesItem(Course coursesItem) {
    if (this.courses == null) {
      this.courses = new ArrayList<>();
    }
    this.courses.add(coursesItem);
    return this;
  }

  /**
   * Get courses
   * @return courses
  */
  @Valid 
  @Schema(name = "courses", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("courses")
  public List<@Valid Course> getCourses() {
    return courses;
  }

  public void setCourses(List<@Valid Course> courses) {
    this.courses = courses;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ListCoursesResponse listCoursesResponse = (ListCoursesResponse) o;
    return Objects.equals(this.courses, listCoursesResponse.courses);
  }

  @Override
  public int hashCode() {
    return Objects.hash(courses);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ListCoursesResponse {\n");
    sb.append("    courses: ").append(toIndentedString(courses)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

