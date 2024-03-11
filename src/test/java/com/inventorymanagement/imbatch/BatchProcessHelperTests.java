package com.inventorymanagement.imbatch;


import com.github.javafaker.Faker;
import com.inventorymanagement.imbatch.models.dto.ProductDetails;
import com.inventorymanagement.imbatch.models.enums.Action;
import com.inventorymanagement.imbatch.models.enums.Frequency;
import com.inventorymanagement.imbatch.repository.DatabaseRepository;
import com.inventorymanagement.imbatch.services.BatchProcessHelper;
import com.inventorymanagement.imbatch.utilities.UpdateOnDays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class BatchProcessHelperTests {

  @InjectMocks
  private BatchProcessHelper batchProcessHelper;

  @Mock
  private DatabaseRepository databaseRepository;

  @Test
  public void testGetNewQuantity_Increment() {
    assertEquals(5.0, BatchProcessHelper.getNewQuantity(Action.INCREMENT, 3.0, 2.0));
  }

  @Test
  public void testGetNewQuantity_Increment2() {
    assertEquals(0.0, BatchProcessHelper.getNewQuantity(Action.INCREMENT, 0.0, 0.0));
  }

  @Test
  public void testGetNewQuantity_Decrement1() {
    assertEquals(1.0, BatchProcessHelper.getNewQuantity(Action.DECREMENT, 3.0, 2.0));
  }

  @Test
  public void testGetNewQuantity_Decrement2() {
    assertEquals(0.0, BatchProcessHelper.getNewQuantity(Action.DECREMENT, 0.0, 2.0));
  }

  @Test
  public void testGetNewQuantity_Decrement3() {
    assertEquals(0.0, BatchProcessHelper.getNewQuantity(Action.DECREMENT, 0.0, 0.0));
  }

  @Test
  public void testGetNewQuantity_Decrement4() {
    assertEquals(0.0, BatchProcessHelper.getNewQuantity(Action.DECREMENT, 2.0, 4.0));
  }

  @Test
  public void testGetNewQuantity_Set1() {
    assertEquals(2.0, BatchProcessHelper.getNewQuantity(Action.SET, 3.0, 2.0));
  }

  @Test
  public void testGetNewQuantity_Set2() {
    assertEquals(2.0, BatchProcessHelper.getNewQuantity(Action.SET, 0.0, 2.0));
  }

  @Test
  public void testGetNextRun() {
    assertNull(BatchProcessHelper.getNextRun(null, null, null));
  }

  @Test
  public void testGetNextRun_Weekly() {
    LocalDate mockNow = LocalDate.of(2024, 1, 1);

    LocalDate expectedDate = LocalDate.of(2024, 1, 8);
    LocalDate actual = BatchProcessHelper.getNextRun(Frequency.WEEKLY, null, mockNow);

    assertEquals(expectedDate, actual);
  }

  @Test
  public void testGetNextRun_DailyNull() {
    assertNull(BatchProcessHelper.getNextRun(Frequency.DAILY, null, null));
    assertNull(BatchProcessHelper.getNextRun(Frequency.DAILY, new ArrayList<>(), LocalDate.now()));
  }

  private static List<ProductDetails> createProductDetailsList() {
    Faker faker = new Faker();

    ProductDetails product1 = ProductDetails.builder()
            .id(1)
            .userId(faker.name().username())
            .currentQuantity(3.0)
            .quantityToChange(2.0)
            .action(Action.INCREMENT)
            .frequency(Frequency.DAILY)
            .updateOn(new ArrayList<>(List.of(UpdateOnDays.WEDNESDAY, UpdateOnDays.THURSDAY, UpdateOnDays.FRIDAY, UpdateOnDays.SATURDAY)))
            .build();

    ProductDetails product2 = ProductDetails.builder()
            .id(2)
            .userId(faker.name().username())
            .currentQuantity(3.0)
            .quantityToChange(2.0)
            .action(Action.DECREMENT)
            .frequency(Frequency.WEEKLY)
            .updateOn(null)
            .build();

    ProductDetails product3 = ProductDetails.builder()
            .id(3)
            .userId(faker.name().username())
            .currentQuantity(3.0)
            .quantityToChange(2.0)
            .action(Action.SET)
            .frequency(Frequency.DAILY)
            .updateOn(null)
            .build();

    ProductDetails product4 = ProductDetails.builder()
            .id(4)
            .userId(faker.name().username())
            .currentQuantity(3.0)
            .quantityToChange(2.0)
            .action(Action.INCREMENT)
            .frequency(Frequency.WEEKLY)
            .updateOn(null)
            .build();

    return new ArrayList<>(List.of(product1, product2, product3, product4));
  }
}
