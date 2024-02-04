package com.inventorymanagement.imbatch.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum ShoppingListReason {
  OUT_OF_STOCK("OUT_OF_STOCK"),
  LOW_STOCK("LOW_STOCK"),
  EXPIRATION("EXPIRATION"),
  MANUAL("MANUAL"),
  OTHER("OTHER");

  private final String value;

  ShoppingListReason(String name) {
    this.value = name;
  }

  @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
  public static ShoppingListReason fromString(String text) {
    for (ShoppingListReason b : ShoppingListReason.values()) {
      if (b.value.equalsIgnoreCase(text)) {
        return b;
      }
    }
    return null;
  }

  public static ShoppingListReason fromTask(Task task) {
    return switch (task) {
      case EXPIRATION -> EXPIRATION;
      case LOW_STOCK -> LOW_STOCK;
      default -> OTHER;
    };
  }
}