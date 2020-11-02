package com.anasdidi.budget.api.expense;

import io.vertx.core.json.JsonObject;

class ExpenseVO {

  final String id;
  final String item;
  final Double price;

  ExpenseVO(String id, String item, Double price) {
    this.id = id;
    this.item = item;
    this.price = price;
  }

  static ExpenseVO fromJson(JsonObject json) {
    String id = json.getString("id", json.getString("_id"));
    String item = json.getString("item");
    Double price = json.getDouble("price");

    return new ExpenseVO(id, item, price);
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
