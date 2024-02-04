package com.inventorymanagement.imbatch.utilities.sql;

import com.inventorymanagement.imbatch.utilities.Constants;

public class SQL {

  public static String getOLDQuery(int bitmask) {
    return String.format("""
            SELECT p.id,
            p.current_quantity,
            p.name,
            p.default_quantity,
            p.low_stock_threshold,
            p.user_id,
            p.expiration_notice_days,
            p.expiration_date,

            qs.frequency,
            qs.action,
            qs.quantity_to_change,
            qs.start_date,
            qs.end_date,
            qs.next_run,
            qs.last_run,
            qs.update_on
            FROM Product p
            JOIN QuantityScheduler qs ON p.id = qs.product_id
                        
            WHERE (p.current_quantity != 0 AND qs.action = 'DECREMENT')
            AND ((qs.frequency = 'Daily' and qs.update_on & %d > 0) OR (qs.start_date = CURDATE()))
            AND qs.is_active = true
            AND qs.end_date >= CURDATE()
            AND qs.is_admin_disabled = false
            ORDER BY p.id""", bitmask);
  }

  public static String getQuery(int bitmask) {
    return String.format("""
            SELECT p.id,
                   p.current_quantity,
                   p.default_quantity,
                   p.low_stock_threshold,
                   p.user_id,
                   p.name,
                   p.expiration_notice_days,
                   p.expiration_date,
                   qs.id as scheduler_id,
                   qs.frequency,
                   qs.action,
                   qs.quantity_to_change,
                   qs.start_date,
                   qs.end_date,
                   qs.next_run,
                   qs.last_run,
                   qs.update_on
            FROM Product p
                     JOIN QuantityScheduler qs ON p.id = qs.product_id
            WHERE qs.is_active = true
                AND qs.is_admin_disabled = false
                AND (qs.end_date IS NULL OR qs.end_date >= CURDATE())
                AND ((qs.action = 'DECREMENT' AND p.current_quantity > 0) OR (qs.action IN ('INCREMENT', 'SET')))
                AND ((qs.frequency = 'DAILY' AND qs.update_on & %d > 0) OR (qs.start_date = CURDATE()))
            ORDER BY p.id;""", bitmask);
  }

  public static String getSaveAuditQuery() {
    return String.format("""
            INSERT INTO SchedulerAudit (user_id, product_id, scheduler_id, action, new_quantity, old_quantity)
            VALUES (:%s, :%s, :%s, :%s, :%s, :%s)
            """, Constants.USER_ID, Constants.PRODUCT_ID, Constants.SCHEDULER_ID, Constants.ACTION, Constants.NEW_QUANTITY, Constants.OLD_QUANTITY);
  }


  public static String getUpdateProductQuery() {
    return String.format("""
            UPDATE Product
            SET current_quantity = :%s
            WHERE id = :%s
            """, Constants.NEW_QUANTITY, Constants.PRODUCT_ID);
  }

  public static String getUpdateSchedulerQuery() {
    return String.format("""
            UPDATE QuantityScheduler
            SET next_run = :%s, last_run = :%s, is_active = :%s
            WHERE id = :%s
            """, Constants.NEXT_RUN, Constants.LAST_RUN, Constants.IS_ACTIVE, Constants.SCHEDULER_ID);
  }

  public static String getSaveNotificationQuery() {
    return String.format("""
            INSERT INTO Notifications (notification_message, product_id, user_id, notification_type)
            VALUES (:%s, :%s, :%s, :%s)
            """, Constants.MESSAGE, Constants.PRODUCT_ID, Constants.USER_ID, Constants.NOTIFICATION_TYPE);
  }

  public static String getNotificationQuery() {
    return String.format("""
            SELECT n.id, n.notification_message, n.product_id, n.user_id, n.notification_type, n.notification_status
            FROM Notifications n
            WHERE n.product_id IN (:%s)
            AND (notification_status = 'UNREAD' OR n.acknowledged_at >= (CURDATE() - INTERVAL 7 DAY))
            """, Constants.PRODUCT_ID);
  }

  public static String getProductTasks() {
    return String.format("""
            SELECT *
            FROM ProductTasks
            WHERE product_id IN (:%s)
            """, Constants.PRODUCT_ID);
  }

  public static String saveShoppingListProduct() {
    return String.format("""
            INSERT INTO ShoppingListProducts (reason, user_id, product_id)
            VALUES (:%s, :%s, :%s)
            """, Constants.REASON, Constants.USER_ID, Constants.PRODUCT_ID);
  }

  public static String getShoppingListProducts() {
    return String.format("""
            SELECT id, product_id, user_id, reason
            FROM ShoppingListProducts
            WHERE product_id IN (:%s)
            """, Constants.PRODUCT_ID);
  }
}


