package com.inventorymanagement.imbatch.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.inventorymanagement.imbatch.utilities.ConsoleFormatter;
import com.inventorymanagement.imbatch.utilities.UpdateOnDays;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

import static com.inventorymanagement.imbatch.utilities.ConsoleFormatter.printColored;

@Getter
public enum Frequency {
  DAILY("Daily"),
  WEEKLY("Weekly"),
  BI_WEEKLY("BiWeekly"),
  MONTHLY("Monthly"),
  BI_MONTHLY("BiMonthly"),
  QUARTERLY("Quarterly"),
  SEMI_ANNUALLY("SemiAnnually"),
  ANNUALLY("Annually");

  private final String value;

  Frequency(String name) {
    this.value = name;
  }

  @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
  public static Frequency fromString(String text) {
    for (Frequency b : Frequency.values()) {
      if (b.value.equalsIgnoreCase(text)) {
        return b;
      }
    }
    printColored("NO MATCH FOUND FOR " + text + " IN SchedulerFrequency ENUM", ConsoleFormatter.Color.RED, false);
    return null;
  }

  public int getDays() {
    return switch (this) {
      case DAILY -> 1;
      case WEEKLY -> 7;
      case BI_WEEKLY -> 14;
      case MONTHLY -> 30;
      case BI_MONTHLY -> 60;
      case QUARTERLY -> 90;
      case SEMI_ANNUALLY -> 180;
      case ANNUALLY -> 365;
    };
  }




}
