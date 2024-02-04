package com.inventorymanagement.imbatch.services;

import com.inventorymanagement.imbatch.models.ProductDetailsStatistics;
import com.inventorymanagement.imbatch.models.db.Notification;
import com.inventorymanagement.imbatch.models.db.ProductTask;
import com.inventorymanagement.imbatch.models.db.QuantityScheduler;
import com.inventorymanagement.imbatch.models.db.ShoppingListProduct;
import com.inventorymanagement.imbatch.models.dto.ProductDetails;
import com.inventorymanagement.imbatch.models.enums.Action;
import com.inventorymanagement.imbatch.models.enums.Frequency;
import com.inventorymanagement.imbatch.models.enums.NotificationType;
import com.inventorymanagement.imbatch.models.enums.ShoppingListReason;
import com.inventorymanagement.imbatch.repository.DatabaseRepository;
import com.inventorymanagement.imbatch.services.batch.BatchProcessEvent;
import com.inventorymanagement.imbatch.utilities.ConsoleFormatter;
import com.inventorymanagement.imbatch.utilities.Constants;
import com.inventorymanagement.imbatch.utilities.UpdateOnDays;
import com.inventorymanagement.imbatch.utilities.Utilities;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.inventorymanagement.imbatch.utilities.ConsoleFormatter.printColored;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchProcessEventListener {
  private final DatabaseRepository databaseRepository;
  private JobLauncher jobLauncher;
  private Job exampleJob;


  public static double getNewQuantity(Action action, double currentQuantity, double quantityToChange) {
    return switch (action) {
      case INCREMENT -> currentQuantity + quantityToChange;
      case DECREMENT -> Math.max(0, currentQuantity - quantityToChange);
      case SET -> quantityToChange;
    };
  }

  public static LocalDate getNextRun(Frequency frequency, List<UpdateOnDays> daysToRun) {
    if (frequency == null) {
      return null;
    }

    if (frequency == Frequency.DAILY && !daysToRun.isEmpty()) {
      return UpdateOnDays.getNextRun(daysToRun);
    } else if (frequency == Frequency.DAILY) {
      return null;
    }

    return LocalDate.now().plusDays(frequency.getDays());

  }

  @Async
  @EventListener
  public void onBatchProcessEvent(BatchProcessEvent event) {
    printColored("Batch process triggered", ConsoleFormatter.Color.CYAN);
    List<ProductDetails> productDetails = databaseRepository.getProductInfo(UpdateOnDays.getToday());

    if (productDetails.isEmpty()) {
      printColored("No products to process", ConsoleFormatter.Color.CYAN);
      return;
    }

    List<ProductDetails> productsToSave = productDetails.stream().map(item -> {
      double oldQuantity = item.getCurrentQuantity();
      double newQuantity = getNewQuantity(item.getAction(), item.getCurrentQuantity(), item.getQuantityToChange());
      return ProductDetails.of(item, newQuantity, getNextRun(item.getFrequency(), item.getUpdateOn()), LocalDate.now(), oldQuantity);
    }).toList();

    saveProducts(productsToSave);
    saveSchedulerAudit(productsToSave);
    saveQuantityScheduler(productsToSave);

    List<Integer> productIds = productsToSave.stream().map(ProductDetails::getId).toList();
    Map<String, List<ProductDetails>> productDetailsByUser = productDetails.stream().collect(Collectors.groupingBy(ProductDetails::getUserId));
    Map<String, List<ProductTask>> productTasks = databaseRepository.getProductTasks(productIds).stream().collect(Collectors.groupingBy(ProductTask::getUserId));
    Map<String, List<ShoppingListProduct>> shoppingListProducts = databaseRepository.getShoppingListProducts(productIds).stream().collect(Collectors.groupingBy(ShoppingListProduct::getUserId));
    Map<String, List<Notification>> existingNotifications = databaseRepository.getNotifications(productIds).stream().collect(Collectors.groupingBy(Notification::getUserId));

    ProductDetailsStatistics stats = ProductDetailsStatistics.builder()
            .productDetails(productDetails)
            .productDetailsByUser(productDetailsByUser)
            .productTasks(productTasks)
            .shoppingListProducts(shoppingListProducts)
            .existingNotifications(existingNotifications)
            .notificationsToSave(new ArrayList<>())
            .shoppingListProductsToSave(new ArrayList<>())
            .build()
            .execute();

    saveNotifications(stats.getNotificationsToSave());
    saveShoppingListProducts(stats.getShoppingListProductsToSave());

    printColored("Batch process complete", ConsoleFormatter.Color.CYAN);
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
