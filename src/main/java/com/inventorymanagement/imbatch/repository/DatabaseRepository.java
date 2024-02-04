package com.inventorymanagement.imbatch.repository;

import com.inventorymanagement.imbatch.models.db.Notification;
import com.inventorymanagement.imbatch.models.db.ProductTask;
import com.inventorymanagement.imbatch.models.db.QuantityScheduler;
import com.inventorymanagement.imbatch.models.db.ShoppingListProduct;
import com.inventorymanagement.imbatch.models.dto.ProductDetails;
import com.inventorymanagement.imbatch.utilities.Constants;
import com.inventorymanagement.imbatch.utilities.UpdateOnDays;
import com.inventorymanagement.imbatch.utilities.sql.SQL;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DatabaseRepository {

  private final NamedParameterJdbcTemplate jdbcTemplate;

  public List<ProductDetails> getProductInfo(UpdateOnDays day) {
    return jdbcTemplate.query(SQL.getQuery(day.getBitmask()), DatabaseMapper.mapToProductDetails());
  }

  public void saveSchedulerAudit(List<ProductDetails> productDetails) {
    List<SqlParameterSource> params = new ArrayList<>();

    for (ProductDetails details : productDetails) {
      params.add(getSchedulerAuditParams(details));
    }

    jdbcTemplate.batchUpdate(SQL.getSaveAuditQuery(), params.toArray(new SqlParameterSource[0]));
  }

  public void saveUpdatedProduct(List<ProductDetails> productDetails) {
    List<SqlParameterSource> params = new ArrayList<>();

    for (ProductDetails details : productDetails) {
      params.add(new MapSqlParameterSource()
              .addValue(Constants.PRODUCT_ID, details.getId())
              .addValue(Constants.NEW_QUANTITY, details.getCurrentQuantity()));
    }

    jdbcTemplate.batchUpdate(SQL.getUpdateProductQuery(), params.toArray(new SqlParameterSource[0]));
  }

  private MapSqlParameterSource getSchedulerAuditParams(ProductDetails productDetails) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue(Constants.PRODUCT_ID, productDetails.getId());
    params.addValue(Constants.USER_ID, productDetails.getUserId());
    params.addValue(Constants.SCHEDULER_ID, productDetails.getSchedulerId());
    params.addValue(Constants.ACTION, productDetails.getAction().getValue());
    params.addValue(Constants.NEW_QUANTITY, productDetails.getCurrentQuantity());
    params.addValue(Constants.OLD_QUANTITY, productDetails.getOldQuantity());
    return params;
  }

  public void saveQuantityScheduler(List<QuantityScheduler> quantitySchedulers) {
    List<SqlParameterSource> params = new ArrayList<>();

    for (QuantityScheduler quantityScheduler : quantitySchedulers) {
      params.add(new MapSqlParameterSource()
              .addValue(Constants.SCHEDULER_ID, quantityScheduler.getId())
              .addValue(Constants.NEXT_RUN, quantityScheduler.getNextRun())
              .addValue(Constants.LAST_RUN, quantityScheduler.getLastRun())
              .addValue(Constants.IS_ACTIVE, quantityScheduler.isActive()));
    }

    jdbcTemplate.batchUpdate(SQL.getUpdateSchedulerQuery(), params.toArray(new SqlParameterSource[0]));
  }

  public void saveNotifications(List<Notification> notifications) {
    List<SqlParameterSource> params = new ArrayList<>();

    for (Notification notification : notifications) {
      params.add(new MapSqlParameterSource()
              .addValue(Constants.MESSAGE, notification.getNotificationMessage())
              .addValue(Constants.PRODUCT_ID, notification.getProductId())
              .addValue(Constants.USER_ID, notification.getUserId())
              .addValue(Constants.NOTIFICATION_TYPE, notification.getNotificationType().getValue()));
    }

    jdbcTemplate.batchUpdate(SQL.getSaveNotificationQuery(), params.toArray(new SqlParameterSource[0]));
  }

  public List<Notification> getNotifications(List<Integer> productIds) {
    MapSqlParameterSource params = new MapSqlParameterSource();

    params.addValue(Constants.PRODUCT_ID, productIds);

    return jdbcTemplate.query(SQL.getNotificationQuery(), params, DatabaseMapper.mapToNotification());
  }

  public List<ProductTask> getProductTasks(List<Integer> productIds) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue(Constants.PRODUCT_ID, productIds);
    return jdbcTemplate.query(SQL.getProductTasks(), params, DatabaseMapper.mapToProductTask());
  }

  public void saveShoppingListProducts(List<ShoppingListProduct> shoppingListProducts) {
    List<SqlParameterSource> params = new ArrayList<>();

    for (ShoppingListProduct product : shoppingListProducts) {
      params.add(getShoppingListParams(product));
    }

    jdbcTemplate.batchUpdate(SQL.saveShoppingListProduct(), params.toArray(new SqlParameterSource[0]));

  }

  private SqlParameterSource getShoppingListParams(ShoppingListProduct product) {
    return new MapSqlParameterSource()
            .addValue(Constants.REASON, product.getReason().getValue())
            .addValue(Constants.USER_ID, product.getUserId())
            .addValue(Constants.PRODUCT_ID, product.getProductId());
  }

  public List<ShoppingListProduct> getShoppingListProducts(List<Integer> productIds) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue(Constants.PRODUCT_ID, productIds);
    return jdbcTemplate.query(SQL.getShoppingListProducts(), params, DatabaseMapper.mapToShoppingListProduct());
  }
}
