package com.anasdidi.budget.api.graphql.dto;

import graphql.schema.DataFetchingEnvironment;
import io.vertx.core.json.JsonObject;

public class ExpenseDTO {

  private final String id;
  private final String item;
  private final Double price;
  private final String createDate;
  private final String updateDate;
  private final Long version;

  public static ExpenseDTO fromJson(JsonObject json) {
    String id = json.getString("_id");
    String item = json.getString("item");
    Double price = json.getDouble("price");
    String createDate = json.getString("createDate");
    String updateDate = json.getString("updateDate");
    Long version = json.getLong("version");

    return new ExpenseDTO(id, item, price, createDate, updateDate, version);
  }

  public ExpenseDTO(String id, String item, Double price, String createDate, String updateDate,
      Long version) {
    this.id = id;
    this.item = item;
    this.price = price;
    this.createDate = createDate;
    this.updateDate = updateDate;
    this.version = version;
  }

  public String getId(DataFetchingEnvironment environment) {
    return id;
  }

  public String getItem(DataFetchingEnvironment environment) {
    return item;
  }

  public Double getPrice(DataFetchingEnvironment environment) {
    return price;
  }

  public String getCreateDate(DataFetchingEnvironment environment) {
    return createDate;
  }

  public String getUpdateDate(DataFetchingEnvironment environment) {
    return updateDate;
  }

  public Long getVersion(DataFetchingEnvironment environment) {
    return version;
  }
}
