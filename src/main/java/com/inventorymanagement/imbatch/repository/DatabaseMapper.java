package com.inventorymanagement.imbatch.repository;

import com.inventorymanagement.imbatch.models.db.Notification;
import com.inventorymanagement.imbatch.models.db.ProductTask;
import com.inventorymanagement.imbatch.models.db.ShoppingListProduct;
import com.inventorymanagement.imbatch.models.dto.ProductDetails;
import com.inventorymanagement.imbatch.models.enums.Action;
import com.inventorymanagement.imbatch.models.enums.Frequency;
import com.inventorymanagement.imbatch.models.enums.NotificationStatus;
import com.inventorymanagement.imbatch.models.enums.NotificationType;
import com.inventorymanagement.imbatch.models.enums.ShoppingListReason;
import com.inventorymanagement.imbatch.models.enums.Task;
import com.inventorymanagement.imbatch.utilities.UpdateOnDays;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class DatabaseMapper {

  public static RowMapper<ProductDetails> mapToProductDetails() {
    return (rs, rowNum) -> ProductDetails.builder()
            .id(rs.getInt("id"))
            .schedulerId(rs.getInt("scheduler_id"))
            .name(rs.getString("name"))
            .currentQuantity(rs.getDouble("current_quantity"))
            .defaultQuantity(rs.getDouble("default_quantity"))
            .lowStockThreshold(rs.getDouble("low_stock_threshold"))
            .userId(rs.getString("user_id"))
            .expirationNoticeDays(rs.getInt("expiration_notice_days"))
            .frequency(Frequency.fromString(rs.getString("frequency")))
            .action(Action.fromString(rs.getString("action")))
            .quantityToChange(rs.getDouble("quantity_to_change"))
            .updateOn(UpdateOnDays.getDaysFromBitmask(rs.getInt("update_on")))
            .expirationDate(getDate(rs.getDate("expiration_date")))
            .startDate(getDate(rs.getDate("start_date")))
            .endDate(getDate(rs.getDate("end_date")))
            .nextRun(getDate(rs.getDate("next_run")))
            .lastRun(getDate(rs.getDate("last_run")))
            .build();
  }

  private static LocalDate getDate(Date date) {
    return date != null ? date.toLocalDate() : null;
  }

  public static RowMapper<Notification> mapToNotification() {
    return (rs, rowNum) -> Notification.builder()
            .id(rs.getInt("id"))
            .userId(rs.getString("user_id"))
            .notificationType(NotificationType.fromString(rs.getString("notification_type")))
            .createdAt(rs.getString("created_at"))
            .build();
  }

  public static RowMapper<ProductTask> mapToProductTask() {
    return (rs, rowNum) -> ProductTask.builder()
            .id(rs.getInt("id"))
            .productId(rs.getInt("product_id"))
            .task(Task.fromString(rs.getString("task")))
            .userId(rs.getString("user_id"))
            .addToList(rs.getBoolean("add_to_list"))
            .createdAt(rs.getString("created_at"))
            .updatedAt(rs.getString("updated_at"))
            .build();
  }

  public static RowMapper<ShoppingListProduct> mapToShoppingListProduct() {
    return (rs, rowNum) -> ShoppingListProduct.builder()
            .id(rs.getInt("id"))
            .userId(rs.getString("user_id"))
            .productId(rs.getInt("product_id"))
            .reason(ShoppingListReason.fromString(rs.getString("reason")))
            .build();
  }
}
