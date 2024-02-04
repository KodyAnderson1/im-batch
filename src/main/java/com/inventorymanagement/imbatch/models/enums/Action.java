package com.inventorymanagement.imbatch.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum Action {
  SET("SET"),
  INCREMENT("INCREMENT"),
  DECREMENT("DECREMENT");

  private final String value;

  Action(String name) {
    this.value = name;
  }

  @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
  public static Action fromString(String text) {
    for (Action b : Action.values()) {
      if (b.value.equalsIgnoreCase(text)) {
        return b;
      }
    }
    return null;
  }
}
