package com.inventorymanagement.imbatch.models.db;

import com.inventorymanagement.imbatch.models.dto.ProductDetails;
import com.inventorymanagement.imbatch.models.enums.QuantityChangeCause;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuantityChange {
  private int id;
  private double oldQuantity;
  private double newQuantity;
  private QuantityChangeCause cause;
  private String createdAt;
  private String userId;
  private int productId;

  public static QuantityChange of(ProductDetails details) {
    return QuantityChange.builder()
            .oldQuantity(details.getOldQuantity())
            .newQuantity(details.getCurrentQuantity())
            .cause(QuantityChangeCause.TASK)
            .userId(details.getUserId())
            .productId(details.getId())
            .build();
  }
}