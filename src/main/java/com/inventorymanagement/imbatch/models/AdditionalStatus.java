package com.inventorymanagement.imbatch.models;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AdditionalStatus {
  private String status;
  private String message;
}
