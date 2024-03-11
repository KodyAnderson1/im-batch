package com.inventorymanagement.imbatch.services;

import com.inventorymanagement.imbatch.models.ProductDetailsStatistics;
import com.inventorymanagement.imbatch.models.db.Notification;
import com.inventorymanagement.imbatch.models.db.ProductTask;
import com.inventorymanagement.imbatch.models.db.ShoppingListProduct;
import com.inventorymanagement.imbatch.models.dto.ProductDetails;
import com.inventorymanagement.imbatch.repository.DatabaseRepository;
import com.inventorymanagement.imbatch.services.batch.BatchProcessEvent;
import com.inventorymanagement.imbatch.utilities.ConsoleFormatter;
import com.inventorymanagement.imbatch.utilities.UpdateOnDays;
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

  private final BatchProcessHelper batchProcessHelper;

  //  private JobLauncher jobLauncher;
  //  private Job exampleJob;

  @Async
  @EventListener
  public void onBatchProcessEvent(BatchProcessEvent event) {
    printColored("Batch process triggered", ConsoleFormatter.Color.CYAN);
    List<ProductDetails> productDetails = batchProcessHelper.getProductInfo(UpdateOnDays.getToday());
    List<ProductDetails> productsToSave = BatchProcessHelper.processProducts(productDetails);

    if (productsToSave.isEmpty()) {
      printColored("No products to save", ConsoleFormatter.Color.CYAN);
      return;
    }

    printColored(productDetails.size() + " items to process", ConsoleFormatter.Color.CYAN);

    batchProcessHelper.saveProducts(productsToSave);
    batchProcessHelper.saveSchedulerAudit(productsToSave);
    batchProcessHelper.saveQuantityScheduler(productsToSave);

    List<Integer> productIds = productsToSave.stream().map(ProductDetails::getId).toList();
    Map<String, List<ProductDetails>> productDetailsByUser = BatchProcessHelper.getProductDetailsByUser(productDetails);
    Map<String, List<ProductTask>> productTasks = batchProcessHelper.getProductTasksByUserId(productIds);
    Map<String, List<ShoppingListProduct>> shoppingListProducts = batchProcessHelper.getShoppingListProductsByUserId(productIds);
    Map<String, List<Notification>> existingNotifications = batchProcessHelper.getNotificationsByUserId(productIds);

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

    batchProcessHelper.saveNotifications(stats.getNotificationsToSave());
    batchProcessHelper.saveShoppingListProducts(stats.getShoppingListProductsToSave());

    printColored("Batch process complete", ConsoleFormatter.Color.CYAN);
  }

}
