package com.inventorymanagement.imbatch.utilities;

public class Constants {
  public static final String PRODUCT_ID = "productId";
  public static final String USER_ID = "userId";
  public static final String SCHEDULER_ID = "schedulerId";
  public static final String ACTION = "action";
  public static final String NEW_QUANTITY = "newQuantity";
  public static final String OLD_QUANTITY = "oldQuantity";
  public static final String NEXT_RUN = "nextRun";
  public static final String LAST_RUN = "lastRun";
  public static final String IS_ACTIVE = "isActive";
  public static final String MESSAGE = "message";
  public static final String NOTIFICATION_TYPE = "notificationType";
  public static final String REASON = "reason";

  public static String LOW_STOCK_MESSAGE(String productName) {
    return String.format("%s has low stock!", productName);
  }

  public static String EXPIRED_MESSAGE(String productName) {
    return String.format("%s has expired!", productName);
  }

  public static String EXPIRATION_NOTICE_MESSAGE(String productName, int days) {
    return String.format("%s is about to expire in %d days!", productName, days);
  }

  public static String OUT_OF_STOCK_MESSAGE(String productName) {
    return String.format("%s is out of stock!", productName);
  }





}
