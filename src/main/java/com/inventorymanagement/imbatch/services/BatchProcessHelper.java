package com.inventorymanagement.imbatch.services;

import com.inventorymanagement.imbatch.models.db.Notification;
import com.inventorymanagement.imbatch.models.db.ProductTask;
import com.inventorymanagement.imbatch.models.db.QuantityScheduler;
import com.inventorymanagement.imbatch.models.db.ShoppingListProduct;
import com.inventorymanagement.imbatch.models.dto.ProductDetails;
import com.inventorymanagement.imbatch.models.enums.Action;
import com.inventorymanagement.imbatch.models.enums.Frequency;
import com.inventorymanagement.imbatch.repository.DatabaseRepository;
import com.inventorymanagement.imbatch.utilities.ConsoleFormatter;
import com.inventorymanagement.imbatch.utilities.UpdateOnDays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.inventorymanagement.imbatch.utilities.ConsoleFormatter.printColored;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchProcessHelper {

  private final DatabaseRepository databaseRepository;

  public static double getNewQuantity(Action action, double currentQuantity, double quantityToChange) {
    return switch (action) {
      case INCREMENT -> currentQuantity + quantityToChange;
      case DECREMENT -> Math.max(0, currentQuantity - quantityToChange);
      case SET -> quantityToChange;
    };
  }

  public static LocalDate getNextRun(Frequency frequency, List<UpdateOnDays> daysToRun, LocalDate now) {
    if (frequency == null || (frequency == Frequency.DAILY && (daysToRun == null || daysToRun.isEmpty()))) {
      return null;
    }

    if (frequency == Frequency.DAILY) {
      return UpdateOnDays.getNextRun(daysToRun);
    }

    return now.plusDays(frequency.getDays());
  }

  public static List<ProductDetails> processProducts(List<ProductDetails> productDetails) {
    if (productDetails.isEmpty()) {
      return Collections.emptyList();
    }

    return productDetails.stream().map(item -> {
      LocalDate nextRun = getNextRun(item.getFrequency(), item.getUpdateOn(), LocalDate.now());
      double newQuantity = getNewQuantity(item.getAction(), item.getCurrentQuantity(), item.getQuantityToChange());
      return ProductDetails.of(item, newQuantity, nextRun, LocalDate.now());
    }).toList();
  }

  public static Map<String, List<ProductDetails>> getProductDetailsByUser(List<ProductDetails> productDetails) {
    return productDetails.stream().collect(Collectors.groupingBy(ProductDetails::getUserId));
  }

  public List<ProductDetails> getProductInfo(UpdateOnDays updateOnDays) {
    return databaseRepository.getProductInfo(updateOnDays);
  }

  public Map<String, List<ProductTask>> getProductTasksByUserId(List<Integer> productIds) {
    return databaseRepository.getProductTasks(productIds).stream().collect(Collectors.groupingBy(ProductTask::getUserId));
  }

  public Map<String, List<ShoppingListProduct>> getShoppingListProductsByUserId(List<Integer> productIds) {
    return databaseRepository.getShoppingListProducts(productIds).stream().collect(Collectors.groupingBy(ShoppingListProduct::getUserId));
  }

  public Map<String, List<Notification>> getNotificationsByUserId(List<Integer> productIds) {
    return databaseRepository.getNotifications(productIds).stream().collect(Collectors.groupingBy(Notification::getUserId));
  }

  @Async
  public void saveNotifications(List<Notification> notificationsToSave) {

    if (notificationsToSave.isEmpty()) {
      printColored("No notifications to save", ConsoleFormatter.Color.PURPLE);
      return;
    }

    printColored("Saving notifications", ConsoleFormatter.Color.PURPLE);
    databaseRepository.saveNotifications(notificationsToSave);
    printColored("Notifications saved", ConsoleFormatter.Color.PURPLE);
  }

  @Async
  public void saveShoppingListProducts(List<ShoppingListProduct> shoppingListProductsToSave) {
    if (shoppingListProductsToSave.isEmpty()) {
      printColored("No shopping list products to save", ConsoleFormatter.Color.PURPLE);
      return;
    }

    printColored("Saving shopping list products", ConsoleFormatter.Color.PURPLE);
    databaseRepository.saveShoppingListProducts(shoppingListProductsToSave);
    printColored("Shopping list products saved", ConsoleFormatter.Color.PURPLE);
  }

  @Async
  public void saveProducts(List<ProductDetails> productsToSave) {
    printColored("Saving products", ConsoleFormatter.Color.PURPLE);
    databaseRepository.saveUpdatedProduct(productsToSave);
    printColored("Products saved", ConsoleFormatter.Color.PURPLE);
  }

  @Async
  public void saveSchedulerAudit(List<ProductDetails> productsToSave) {
    printColored("Saving scheduler audit", ConsoleFormatter.Color.PURPLE);
    databaseRepository.saveSchedulerAudit(productsToSave);
    printColored("Scheduler audit saved", ConsoleFormatter.Color.PURPLE);
  }

  @Async
  public void saveQuantityScheduler(List<ProductDetails> productsToSave) {
    printColored("Saving quantity scheduler", ConsoleFormatter.Color.PURPLE);

    List<QuantityScheduler> quantitySchedulers = productsToSave.stream().map(item -> {
      boolean isActive = item.getEndDate() == null || item.getEndDate().isAfter(LocalDate.now());
      return QuantityScheduler.of(item, isActive);
    }).toList();

    databaseRepository.saveQuantityScheduler(quantitySchedulers);
    printColored("Quantity scheduler saved", ConsoleFormatter.Color.PURPLE);
  }

}
