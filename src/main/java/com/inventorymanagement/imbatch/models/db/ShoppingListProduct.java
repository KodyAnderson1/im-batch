package com.inventorymanagement.imbatch.models.db;

import com.inventorymanagement.imbatch.models.enums.ShoppingListReason;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShoppingListProduct {
  private int id;
  private int quantity;
  private ShoppingListReason reason;
  private String note;
  private boolean isChecked;
  private String userId;
  private int productId;
  private String createdAt;
  private String updatedAt;

  public static ShoppingListProduct of(String userId, int productId, ShoppingListReason reason) {
    return ShoppingListProduct.builder()
      .userId(userId)
      .productId(productId)
      .reason(reason)
      .build();
  }
}