package com.inventorymanagement.imbatch.utilities;

import com.inventorymanagement.imbatch.models.db.Notification;
import com.inventorymanagement.imbatch.models.db.ShoppingListProduct;
import com.inventorymanagement.imbatch.models.enums.NotificationType;

import java.util.List;
import java.util.Map;

public class ProductDetailsStatisticsUtils {

  public static boolean doesNotHaveNotification(Map<String, List<Notification>> notifications, NotificationType type, String userId, int productId) {
    return !notifications.containsKey(userId) || notifications.get(userId).stream().noneMatch(item -> item.getNotificationType() == type && item.getProductId() == productId);
  }

  // TODO: Refactor this method so it is testable
  public static boolean doesNotHaveSLProduct(Map<String, List<ShoppingListProduct>> shoppingListProducts, int productId, String userId) {
    return !shoppingListProducts.containsKey(userId) || shoppingListProducts.get(userId).stream().noneMatch(item -> item.getProductId() == productId);
  }


}
