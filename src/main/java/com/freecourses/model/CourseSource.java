package com.freecourses.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonValue;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets CourseSource
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-13T00:16:21.000191921+03:00[Europe/Bucharest]")
public enum CourseSource {
  
  YOUTUBE("YOUTUBE"),
  
  COURSERA("COURSERA"),
  
  UDEMY("UDEMY");

  private String value;

  CourseSource(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static CourseSource fromValue(String value) {
    for (CourseSource b : CourseSource.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

