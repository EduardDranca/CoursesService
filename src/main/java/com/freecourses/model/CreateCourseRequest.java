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
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * CreateCourseRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-13T00:16:21.000191921+03:00[Europe/Bucharest]")
public class CreateCourseRequest {

  private String description;

  private URI uri;

  private CourseDifficulty difficulty;

  private String category;

  @Valid
  private List<String> subcategories;

  private CourseSource source;

  /**
   * Default constructor
   * @deprecated Use {@link CreateCourseRequest#CreateCourseRequest(String, URI, String, CourseSource)}
   */
  @Deprecated
  public CreateCourseRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public CreateCourseRequest(String description, URI uri, String category, CourseSource source) {
    this.description = description;
    this.uri = uri;
    this.category = category;
    this.source = source;
  }

  public CreateCourseRequest description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Get description
   * @return description
  */
  @NotNull 
  @Schema(name = "description", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public CreateCourseRequest uri(URI uri) {
    this.uri = uri;
    return this;
  }

  /**
   * Get uri
   * @return uri
  */
  @NotNull @Valid 
  @Schema(name = "uri", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("uri")
  public URI getUri() {
    return uri;
  }

  public void setUri(URI uri) {
    this.uri = uri;
  }

  public CreateCourseRequest difficulty(CourseDifficulty difficulty) {
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

  public CreateCourseRequest category(String category) {
    this.category = category;
    return this;
  }

  /**
   * Get category
   * @return category
  */
  @NotNull @Pattern(regexp = "^([a-zA-Z]| )*$") @Size(min = 1, max = 50) 
  @Schema(name = "category", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("category")
  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public CreateCourseRequest subcategories(List<String> subcategories) {
    this.subcategories = subcategories;
    return this;
  }

  public CreateCourseRequest addSubcategoriesItem(String subcategoriesItem) {
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

  public CreateCourseRequest source(CourseSource source) {
    this.source = source;
    return this;
  }

  /**
   * Get source
   * @return source
  */
  @NotNull @Valid 
  @Schema(name = "source", requiredMode = Schema.RequiredMode.REQUIRED)
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
    CreateCourseRequest createCourseRequest = (CreateCourseRequest) o;
    return Objects.equals(this.description, createCourseRequest.description) &&
        Objects.equals(this.uri, createCourseRequest.uri) &&
        Objects.equals(this.difficulty, createCourseRequest.difficulty) &&
        Objects.equals(this.category, createCourseRequest.category) &&
        Objects.equals(this.subcategories, createCourseRequest.subcategories) &&
        Objects.equals(this.source, createCourseRequest.source);
  }

  @Override
  public int hashCode() {
    return Objects.hash(description, uri, difficulty, category, subcategories, source);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreateCourseRequest {\n");
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

