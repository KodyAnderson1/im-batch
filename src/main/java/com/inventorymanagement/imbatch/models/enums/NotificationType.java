package com.inventorymanagement.imbatch.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum NotificationType {
  OUT_OF_STOCK("OUT_OF_STOCK"),
  LOW_STOCK("LOW_STOCK"),
  EXPIRATION("EXPIRATION"),
  TASK("TASK"),
  SHOPPING_LIST("SHOPPING_LIST"),
  ERROR("ERROR"),
  OTHER("OTHER");

  private final String value;

  NotificationType(String name) {
    this.value = name;
  }

  @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
  public static NotificationType fromString(String text) {
    for (NotificationType b : NotificationType.values()) {
      if (b.value.equalsIgnoreCase(text)) {
        return b;
      }
    }
    return null;
  }
}