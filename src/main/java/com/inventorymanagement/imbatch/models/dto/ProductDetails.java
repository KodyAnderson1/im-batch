package com.inventorymanagement.imbatch.models.dto;

import com.inventorymanagement.imbatch.models.enums.Action;
import com.inventorymanagement.imbatch.models.enums.Frequency;
import com.inventorymanagement.imbatch.utilities.UpdateOnDays;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class ProductDetails {
  private int id;
  private int schedulerId;
  private double currentQuantity;
  private double defaultQuantity;
  private double lowStockThreshold;
  private double oldQuantity;
  private String name;
  private String userId;
  private int expirationNoticeDays;
  private LocalDate expirationDate;
  private Frequency frequency;
  private Action action;
  private double quantityToChange;
  private List<UpdateOnDays> updateOn;
  private LocalDate startDate;
  private LocalDate endDate;
  private LocalDate nextRun;
  private LocalDate lastRun;

  public static ProductDetails of(ProductDetails details, double currQuantity, LocalDate nextRun, LocalDate lastRun) {
    return ProductDetails.builder()
            .id(details.getId())
            .schedulerId(details.getSchedulerId())
            .name(details.getName())
            .defaultQuantity(details.getDefaultQuantity())
            .lowStockThreshold(details.getLowStockThreshold())
            .userId(details.getUserId())
            .expirationNoticeDays(details.getExpirationNoticeDays())
            .expirationDate(details.getExpirationDate())
            .frequency(details.getFrequency())
            .action(details.getAction())
            .quantityToChange(details.getQuantityToChange())
            .updateOn(details.getUpdateOn())
            .startDate(details.getStartDate())
            .endDate(details.getEndDate())
            .currentQuantity(currQuantity)
            .nextRun(nextRun)
            .lastRun(lastRun)

            .oldQuantity(details.getCurrentQuantity())
            .build();
  }
}
