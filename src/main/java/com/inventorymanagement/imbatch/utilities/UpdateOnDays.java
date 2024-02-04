package com.inventorymanagement.imbatch.utilities;

import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
public enum UpdateOnDays {
  SUNDAY(64),
  MONDAY(32),
  TUESDAY(16),
  WEDNESDAY(8),
  THURSDAY(4),
  FRIDAY(2),
  SATURDAY(1);

  private final int bitmask;

  UpdateOnDays(int bitmask) {
    this.bitmask = bitmask;
  }

  public static int getBitmask(List<UpdateOnDays> days) {
    int bitmask = 0;
    for (UpdateOnDays day : days) {
      bitmask |= day.getBitmask();
    }
    return bitmask;
  }

  public LocalDate getNextRun() {
    LocalDate today = LocalDate.now();
    int day = today.getDayOfWeek().getValue();
    int daysToAdd = 0;
    for (int i = 0; i < 7; i++) {
      if ((bitmask & (1 << ((day + i) % 7))) != 0) {
        daysToAdd = i;
        break;
      }
    }
    return today.plusDays(daysToAdd);
  }

  public static LocalDate getNextRun(List<UpdateOnDays> days) {
    LocalDate today = LocalDate.now();
    int dayOfWeek = today.getDayOfWeek().getValue();
    int day = dayOfWeek % 7;
    for (int i = 1; i <= 7; i++) {
      UpdateOnDays nextDay = UpdateOnDays.values()[(day + i) % 7];
      if (days.contains(nextDay)) {
        return today.plusDays(i);
      }
    }
    return today.plusDays(1);
  }


  public static List<UpdateOnDays> getDaysFromBitmask(Integer bitmask) {
    List<UpdateOnDays> days = new ArrayList<>();
    if (bitmask == null) return days;

    for (UpdateOnDays day : UpdateOnDays.values()) {
      if ((bitmask & day.getBitmask()) != 0) {
        days.add(day);
      }
    }
    return days;
  }

  public static UpdateOnDays getToday() {
    int day = LocalDate.now().getDayOfWeek().getValue();
    return UpdateOnDays.values()[day];
  }
}
