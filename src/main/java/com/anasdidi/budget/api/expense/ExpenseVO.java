package com.anasdidi.budget.api.expense;

import java.time.Instant;
import io.vertx.core.json.JsonObject;

class ExpenseVO {

  final String id;
  final String item;
  final Double price;
  final Instant createDate;
  final Long version;

  ExpenseVO(String id, String item, Double price, Instant createDate, Long version) {
    this.id = id;
    this.item = item;
    this.price = price;
    this.createDate = createDate;
    this.version = version;
  }

  static ExpenseVO fromJson(JsonObject json) {
    String id = json.getString("id", json.getString("_id"));
    String item = json.getString("item");
    Double price = json.getDouble("price");
    Instant createDate = json.getInstant("createDate");
    Long version = json.getLong("version");

    return new ExpenseVO(id, item, price, createDate, version);
  }

  static JsonObject toDocument(ExpenseVO vo) {
    JsonObject document = new JsonObject()//
        .put("item", vo.item)//
        .put("price", vo.price);

    if (vo.id != null) {
      document.put("_id", vo.id);
    }

    return document;
  }
}
