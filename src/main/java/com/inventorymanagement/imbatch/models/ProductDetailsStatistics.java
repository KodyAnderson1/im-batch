package com.inventorymanagement.imbatch.models;

import com.inventorymanagement.imbatch.models.db.Notification;
import com.inventorymanagement.imbatch.models.db.ProductTask;
import com.inventorymanagement.imbatch.models.db.ShoppingListProduct;
import com.inventorymanagement.imbatch.models.dto.ProductDetails;
import com.inventorymanagement.imbatch.models.enums.NotificationType;
import com.inventorymanagement.imbatch.models.enums.ShoppingListReason;
import com.inventorymanagement.imbatch.utilities.Constants;
import com.inventorymanagement.imbatch.utilities.Utilities;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@Getter
@Builder
public class ProductDetailsStatistics {

  private List<Notification> notificationsToSave;
  private List<ShoppingListProduct> shoppingListProductsToSave;
  private Map<String, List<ProductDetails>> productDetailsByUser;
  private Map<String, List<ProductTask>> productTasks;
  private Map<String, List<ShoppingListProduct>> shoppingListProducts;
  private Map<String, List<Notification>> existingNotifications;
  private List<ProductDetails> productDetails;

  private static boolean doesNotHaveNotification(Map<String, List<Notification>> notifications, NotificationType type, String userId, int productId) {
    return !notifications.containsKey(userId) || notifications.get(userId).stream().noneMatch(item -> item.getNotificationType() == type && item.getProductId() == productId);
  }

  private static boolean doesNotHaveSLProduct(Map<String, List<ShoppingListProduct>> shoppingListProducts, int productId, String userId) {
    return !shoppingListProducts.containsKey(userId) || shoppingListProducts.get(userId).stream().noneMatch(item -> item.getProductId() == productId);
  }

  public ProductDetailsStatistics execute() {
    for (String userId : productDetailsByUser.keySet()) {
      if (!productTasks.containsKey(userId)) {
        continue;
      }
      List<ProductDetails> userProducts = productDetailsByUser.get(userId);
      List<ProductTask> userProductTasks = productTasks.get(userId);
      for (ProductTask task : userProductTasks) {
        switch (task.getTask()) {
          case EXPIRATION -> checkProductExpirations(userProducts, task, userId);
          case LOW_STOCK -> checkProductStock(userProducts, task, userId);
          case CUSTOM -> log.error("Custom task not implemented");
        }
      }
    }
    return this;
  }

  private void checkProductStock(List<ProductDetails> userProducts, ProductTask task, String userId) {
    ProductDetails product = userProducts.stream().filter(item -> item.getId() == task.getProductId()).findFirst().orElse(null);

    if (product == null) {
      return;
    }

    if (product.getCurrentQuantity() == 0) {

      if (doesNotHaveNotification(existingNotifications, NotificationType.OUT_OF_STOCK, userId, product.getId())) {
        notificationsToSave.add(Notification.of(product.getId(), userId, Constants.OUT_OF_STOCK_MESSAGE(product.getName()), NotificationType.OUT_OF_STOCK));
      }

      if (task.isAddToList() && doesNotHaveSLProduct(shoppingListProducts, product.getId(), userId)) {
        shoppingListProductsToSave.add(ShoppingListProduct.of(userId, product.getId(), ShoppingListReason.OUT_OF_STOCK));
      }

    } else if (product.getCurrentQuantity() <= product.getLowStockThreshold()) {

      if (doesNotHaveNotification(existingNotifications, NotificationType.EXPIRATION, userId, product.getId())) {
        notificationsToSave.add(Notification.of(product.getId(), userId, Constants.LOW_STOCK_MESSAGE(product.getName()), NotificationType.LOW_STOCK));
      }

      if (task.isAddToList() && doesNotHaveSLProduct(shoppingListProducts, product.getId(), userId)) {
        shoppingListProductsToSave.add(ShoppingListProduct.of(userId, product.getId(), ShoppingListReason.LOW_STOCK));
      }

    }
  }

  private void checkProductExpirations(List<ProductDetails> userProducts, ProductTask task, String userId) {
    ProductDetails product = userProducts.stream().filter(item -> item.getId() == task.getProductId()).findFirst().orElse(null);

    if (product == null) {
      return;
    }

    LocalDate expirationDate = product.getExpirationDate();
    if (Utilities.dateNotNullAndExpired(expirationDate)) {

      if (doesNotHaveNotification(existingNotifications, NotificationType.EXPIRATION, userId, product.getId())) {
        notificationsToSave.add(Notification.of(product.getId(), userId, Constants.EXPIRED_MESSAGE(product.getName()), NotificationType.EXPIRATION));
      }

      if (task.isAddToList() && doesNotHaveSLProduct(shoppingListProducts, product.getId(), userId)) {
        shoppingListProductsToSave.add(ShoppingListProduct.of(userId, product.getId(), ShoppingListReason.EXPIRATION));
      }

    } else if (Utilities.dateNotNullAndExpiring(expirationDate, product.getExpirationNoticeDays())) {
      int daysUntilExpiration = Utilities.daysUntilDate(expirationDate);

      if (doesNotHaveNotification(existingNotifications, NotificationType.EXPIRATION, userId, product.getId())) {
        notificationsToSave.add(Notification.of(product.getId(), userId, Constants.EXPIRATION_NOTICE_MESSAGE(product.getName(), daysUntilExpiration), NotificationType.EXPIRATION));
      }

      if (task.isAddToList() && doesNotHaveSLProduct(shoppingListProducts, product.getId(), userId)) {
        shoppingListProductsToSave.add(ShoppingListProduct.of(userId, product.getId(), ShoppingListReason.EXPIRATION));
      }
    }

  }


}


