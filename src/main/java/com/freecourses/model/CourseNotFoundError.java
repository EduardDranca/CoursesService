package com.freecourses.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.UUID;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * CourseNotFoundError
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-13T00:16:21.000191921+03:00[Europe/Bucharest]")
public class CourseNotFoundError {

  private String message;

  private UUID courseId;

  public CourseNotFoundError message(String message) {
    this.message = message;
    return this;
  }

  /**
   * Get message
   * @return message
  */
  
  @Schema(name = "message", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("message")
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public CourseNotFoundError courseId(UUID courseId) {
    this.courseId = courseId;
    return this;
  }

  /**
   * Get courseId
   * @return courseId
  */
  @Valid 
  @Schema(name = "courseId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("courseId")
  public UUID getCourseId() {
    return courseId;
  }

  public void setCourseId(UUID courseId) {
    this.courseId = courseId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CourseNotFoundError courseNotFoundError = (CourseNotFoundError) o;
    return Objects.equals(this.message, courseNotFoundError.message) &&
        Objects.equals(this.courseId, courseNotFoundError.courseId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(message, courseId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CourseNotFoundError {\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    courseId: ").append(toIndentedString(courseId)).append("\n");
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

