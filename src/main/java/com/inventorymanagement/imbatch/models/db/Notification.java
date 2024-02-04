package com.inventorymanagement.imbatch.models.db;

import com.inventorymanagement.imbatch.models.enums.NotificationStatus;
import com.inventorymanagement.imbatch.models.enums.NotificationType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Notification {
  private int id;
  private String createdAt;
  private String acknowledgedAt;

  private NotificationStatus notificationStatus;
  private NotificationType notificationType;
  private String notificationMessage;
  private int productId;
  private String userId;


  public static Notification of(int productId, String userId, String message, NotificationType notificationType) {
    return Notification.builder()
            .userId(userId)
            .productId(productId)
            .notificationType(notificationType)
            .notificationMessage(message)
            .build();
  }
}
