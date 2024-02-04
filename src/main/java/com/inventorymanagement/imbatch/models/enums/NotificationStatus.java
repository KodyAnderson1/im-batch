package com.inventorymanagement.imbatch.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum NotificationStatus {
  UNREAD("UNREAD"),
  READ("READ"),
  DISMISSED("DISMISSED");

  private final String value;

  NotificationStatus(String name) {
    this.value = name;
  }

  @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
  public static NotificationStatus fromString(String text) {
    for (NotificationStatus b : NotificationStatus.values()) {
      if (b.value.equalsIgnoreCase(text)) {
        return b;
      }
    }
    return null;
  }
}