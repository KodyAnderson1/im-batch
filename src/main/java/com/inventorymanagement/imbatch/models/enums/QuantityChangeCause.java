package com.inventorymanagement.imbatch.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum QuantityChangeCause {
  MANUAL("MANUAL"),
  RESTOCK("RESTOCK"),
  TASK("TASK"),
  SHOPPING_LIST("SHOPPING_LIST"),
  EXPIRATION("EXPIRATION"),
  OTHER("OTHER");

  private final String value;

  QuantityChangeCause(String name) {
    this.value = name;
  }

  @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
  public static QuantityChangeCause fromString(String text) {
    for (QuantityChangeCause b : QuantityChangeCause.values()) {
      if (b.value.equalsIgnoreCase(text)) {
        return b;
      }
    }
    return null;
  }
}
