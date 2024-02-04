package com.inventorymanagement.imbatch.models.db;

import com.inventorymanagement.imbatch.models.enums.Task;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class ProductTask {
  private int id;
  private Task task;
  private boolean addToList;
  private String createdAt;
  private String updatedAt;
  private String userId;
  private int productId;
}