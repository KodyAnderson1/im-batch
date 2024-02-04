package com.inventorymanagement.imbatch.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum Task {
  EXPIRATION("EXPIRATION"),
  LOW_STOCK("LOW_STOCK"),
  CUSTOM("CUSTOM");

  private final String value;

  Task(String name) {
    this.value = name;
  }

  @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
  public static Task fromString(String text) {
    for (Task b : Task.values()) {
      if (b.value.equalsIgnoreCase(text)) {
        return b;
      }
    }
    return null;
  }
}
