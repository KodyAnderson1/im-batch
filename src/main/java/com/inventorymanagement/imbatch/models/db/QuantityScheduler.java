package com.inventorymanagement.imbatch.models.db;

import com.inventorymanagement.imbatch.models.dto.ProductDetails;
import com.inventorymanagement.imbatch.models.enums.Action;
import com.inventorymanagement.imbatch.models.enums.Frequency;
import com.inventorymanagement.imbatch.utilities.UpdateOnDays;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class QuantityScheduler {
  private int id;
  private String userId;
  private int productId;
  private Action action;
  private double quantityToChange;
  private Frequency frequency;
  private int updateOn;
  private LocalDate startDate;
  private LocalDate endDate;
  private LocalDate nextRun;
  private LocalDate lastRun;
  private boolean isActive;
  private boolean isAdminDisabled;
  private String createdAt;
  private String updatedAt;

  public static QuantityScheduler of(ProductDetails details, boolean isActive) {
    return QuantityScheduler.builder()
            .id(details.getSchedulerId())
            .userId(details.getUserId())
            .productId(details.getId())
            .action(details.getAction())
            .quantityToChange(details.getQuantityToChange())
            .frequency(details.getFrequency())
            .updateOn(UpdateOnDays.getBitmask(details.getUpdateOn()))
            .startDate(details.getStartDate())
            .endDate(details.getEndDate())
            .nextRun(details.getNextRun())
            .lastRun(details.getLastRun())
            .isActive(isActive)
            .isAdminDisabled(false)
            .build();
  }
}