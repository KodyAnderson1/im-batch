package com.inventorymanagement.imbatch.models.db;

import com.inventorymanagement.imbatch.models.dto.ProductDetails;
import com.inventorymanagement.imbatch.models.enums.Action;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SchedulerAudit {
  private int id;
  private String userId;
  private int productId;
  private int schedulerId;
  private Action action;
  private double newQuantity;
  private double oldQuantity;
  private String createdAt;

  public static SchedulerAudit of(ProductDetails details, int id) {
    return SchedulerAudit.builder()
            .userId(details.getUserId())
            .productId(details.getId())
            .schedulerId(id)
            .action(details.getAction())
            .newQuantity(details.getCurrentQuantity())
            .oldQuantity(details.getOldQuantity())
            .build();
  }
}
