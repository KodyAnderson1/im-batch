package com.inventorymanagement.imbatch.utilities;

import java.time.LocalDate;

public class Utilities {

  public static boolean dateNotNullAndExpired(LocalDate date) {
    return date != null && !date.isBefore(LocalDate.now());
  }

  public static boolean dateNotNullAndExpiring(LocalDate date, int daysOut) {
    return date != null && date.isBefore(LocalDate.now().plusDays(daysOut));
  }

  public static int daysUntilDate(LocalDate expirationDate) {
    return LocalDate.now().until(expirationDate).getDays();
  }
}
