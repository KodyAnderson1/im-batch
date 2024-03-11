package com.inventorymanagement.imbatch.models;

import com.inventorymanagement.imbatch.models.db.Notification;
import com.inventorymanagement.imbatch.models.db.ProductTask;
import com.inventorymanagement.imbatch.models.db.ShoppingListProduct;
import com.inventorymanagement.imbatch.models.dto.ProductDetails;
import com.inventorymanagement.imbatch.models.enums.NotificationType;
import com.inventorymanagement.imbatch.models.enums.ShoppingListReason;
import com.inventorymanagement.imbatch.utilities.Constants;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Slf4j
@Getter
@Builder
public class DetailsStatsV2 {
  @Builder.Default
  private List<Notification> notificationsToSave = new ArrayList<>();
  @Builder.Default
  private List<ShoppingListProduct> shoppingListProductsToSave = new ArrayList<>();

  private Map<String, List<ProductDetails>> productDetailsByUser;
  private Map<String, List<ProductTask>> productTasks;
  private Map<String, List<ShoppingListProduct>> shoppingListProducts;
  private Map<String, List<Notification>> existingNotifications;

  // TODO: Move to a utility class
  private static <T> boolean isAbsent(Map<String, List<T>> map, Predicate<T> condition, String userId) {
    return !map.containsKey(userId) || map.get(userId).stream().noneMatch(condition);
  }

  public DetailsStatsV2 execute() {
    productDetailsByUser.forEach((userId, userProducts) -> productTasks
            .getOrDefault(userId, Collections.emptyList())
            .forEach(task -> processTask(task, userProducts, userId)));
    return this;
  }

  private void processTask(ProductTask task, List<ProductDetails> userProducts, String userId) {
    userProducts.stream()
            .filter(product -> product.getId() == task.getProductId())
            .findFirst()
            .ifPresent(product -> {
              switch (task.getTask()) {
                case EXPIRATION -> checkProductExpirations(product, task, userId);
                case LOW_STOCK -> checkProductStock(product, task, userId);
                default -> log.error("Unhandled task type: {}", task.getTask());
              }
            });
  }

  private void checkProductStock(ProductDetails product, ProductTask task, String userId) {
    if (product.getCurrentQuantity() <= 0) {
      handleOutOfStock(product, task, userId);
    } else if (product.getCurrentQuantity() <= product.getLowStockThreshold()) {
      handleLowStock(product, task, userId);
    }
  }

  private void checkProductExpirations(ProductDetails product, ProductTask task, String userId) {
    LocalDate now = LocalDate.now();
    if (product.getExpirationDate().isBefore(now)) {
      handleExpired(product, task, userId);
    } else if (product.getExpirationDate().minusDays(product.getExpirationNoticeDays()).isBefore(now)) {
      handleExpiring(product, task, userId);
    }
  }

  // Example handler methods
  private void handleOutOfStock(ProductDetails product, ProductTask task, String userId) {

    if (isAbsent(existingNotifications, n -> n.getNotificationType() == NotificationType.OUT_OF_STOCK && n.getProductId() == product.getId(), userId)) {
      notificationsToSave.add(Notification.of(product.getId(), userId, Constants.OUT_OF_STOCK_MESSAGE(product.getName()), NotificationType.OUT_OF_STOCK));
    }

    if (task.isAddToList() && isAbsent(shoppingListProducts, slp -> slp.getProductId() == product.getId(), userId)) {
      shoppingListProductsToSave.add(ShoppingListProduct.of(userId, product.getId(), ShoppingListReason.OUT_OF_STOCK));
    }
  }

  private void handleLowStock(ProductDetails product, ProductTask task, String userId) {
    // Similar logic to handleOutOfStock, adjusted for LOW_STOCK
  }

  private void handleExpired(ProductDetails product, ProductTask task, String userId) {
    // Logic for handling expired products
  }

  private void handleExpiring(ProductDetails product, ProductTask task, String userId) {
    // Logic for handling expiring products
  }

  // Additional methods as needed...
}
