package com.freecourses.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.freecourses.model.CourseDifficulty;
import com.freecourses.model.CourseSource;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Course
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-13T00:16:21.000191921+03:00[Europe/Bucharest]")
public class Course {

  private UUID id;

  private String description;

  private URI uri;

  private CourseDifficulty difficulty;

  private String category;

  @Valid
  private List<String> subcategories;

  private CourseSource source;

  public Course id(UUID id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
  */
  @Valid 
  @Schema(name = "id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Course description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Get description
   * @return description
  */
  
  @Schema(name = "description", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Course uri(URI uri) {
    this.uri = uri;
    return this;
  }

  /**
   * Get uri
   * @return uri
  */
  @Valid 
  @Schema(name = "uri", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("uri")
  public URI getUri() {
    return uri;
  }

  public void setUri(URI uri) {
    this.uri = uri;
  }

  public Course difficulty(CourseDifficulty difficulty) {
    this.difficulty = difficulty;
    return this;
  }

  /**
   * Get difficulty
   * @return difficulty
  */
  @Valid 
  @Schema(name = "difficulty", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("difficulty")
  public CourseDifficulty getDifficulty() {
    return difficulty;
  }

  public void setDifficulty(CourseDifficulty difficulty) {
    this.difficulty = difficulty;
  }

  public Course category(String category) {
    this.category = category;
    return this;
  }

  /**
   * Get category
   * @return category
  */
  @Pattern(regexp = "^([a-zA-Z]| )*$") @Size(min = 1, max = 50) 
  @Schema(name = "category", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("category")
  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public Course subcategories(List<String> subcategories) {
    this.subcategories = subcategories;
    return this;
  }

  public Course addSubcategoriesItem(String subcategoriesItem) {
    if (this.subcategories == null) {
      this.subcategories = new ArrayList<>();
    }
    this.subcategories.add(subcategoriesItem);
    return this;
  }

  /**
   * Get subcategories
   * @return subcategories
  */
  @Size(min = 1, max = 5) 
  @Schema(name = "subcategories", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("subcategories")
  public List<String> getSubcategories() {
    return subcategories;
  }

  public void setSubcategories(List<String> subcategories) {
    this.subcategories = subcategories;
  }

  public Course source(CourseSource source) {
    this.source = source;
    return this;
  }

  /**
   * Get source
   * @return source
  */
  @Valid 
  @Schema(name = "source", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("source")
  public CourseSource getSource() {
    return source;
  }

  public void setSource(CourseSource source) {
    this.source = source;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Course course = (Course) o;
    return Objects.equals(this.id, course.id) &&
        Objects.equals(this.description, course.description) &&
        Objects.equals(this.uri, course.uri) &&
        Objects.equals(this.difficulty, course.difficulty) &&
        Objects.equals(this.category, course.category) &&
        Objects.equals(this.subcategories, course.subcategories) &&
        Objects.equals(this.source, course.source);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, description, uri, difficulty, category, subcategories, source);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Course {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    difficulty: ").append(toIndentedString(difficulty)).append("\n");
    sb.append("    category: ").append(toIndentedString(category)).append("\n");
    sb.append("    subcategories: ").append(toIndentedString(subcategories)).append("\n");
    sb.append("    source: ").append(toIndentedString(source)).append("\n");
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

