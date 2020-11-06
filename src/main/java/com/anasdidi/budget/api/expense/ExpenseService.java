package com.anasdidi.budget.api.expense;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.mongo.MongoClient;

class ExpenseService {

  private static final Logger logger = LogManager.getLogger(ExpenseService.class);
  private final MongoClient mongoClient;

  ExpenseService(MongoClient mongoClient) {
    this.mongoClient = mongoClient;
  }

  Single<String> create(ExpenseVO vo, String requestId) {
    final String TAG = "create";
    JsonObject document = ExpenseVO.toDocument(vo)//
        .put("createDate", Instant.now())//
        .put("version", 0);

    if (logger.isDebugEnabled()) {
      logger.debug("[{}:{}] document\n{}", TAG, requestId, document.encodePrettily());
    }

    return mongoClient.rxSave(ExpenseConstants.COLLECTION_NAME, document).toSingle();
  }

  Single<String> update(ExpenseVO vo, String requestId) {
    final String TAG = "update";
    JsonObject query = new JsonObject()//
        .put("_id", vo.id)//
        .put("version", vo.version);
    JsonObject update = new JsonObject().put("$set", ExpenseVO.toDocument(vo)//
        .put("updateDate", Instant.now())//
        .put("version", vo.version + 1));

    if (logger.isDebugEnabled()) {
      logger.debug("[{}:{}] query\n{}", TAG, requestId, query.encodePrettily());
      logger.debug("[{}:{}] update\n{}", TAG, requestId, update.encodePrettily());
    }

    return mongoClient.rxFindOneAndUpdate(ExpenseConstants.COLLECTION_NAME, query, update)//
        .map(doc -> doc.getString("_id"))//
        .toSingle();
  }

  Single<String> delete(ExpenseVO vo, String requestId) {
    final String TAG = "delete";
    JsonObject query = new JsonObject()//
        .put("_id", vo.id)//
        .put("version", vo.version);

    if (logger.isDebugEnabled()) {
      logger.debug("[{}:{}] query\n{}", TAG, requestId, query.encodePrettily());
    }

    return mongoClient.rxFindOneAndDelete(ExpenseConstants.COLLECTION_NAME, query)//
        .map(doc -> doc.getString("_id"))//
        .toSingle();
  }

  Single<ExpenseVO> getExpenseById(ExpenseVO vo, String requestId) {
    final String TAG = "getExpenseById";
    JsonObject query = new JsonObject();
    JsonObject fields = new JsonObject();

    if (logger.isDebugEnabled()) {
      logger.debug("[{}:{}] query\n{}", TAG, requestId, query.encodePrettily());
      logger.debug("[{}:{}] fields\n{}", TAG, requestId, fields.encodePrettily());
    }

    return mongoClient.rxFindOne(ExpenseConstants.COLLECTION_NAME, query, fields)//
        .map(json -> ExpenseVO.fromJson(json))//
        .toSingle();
  }

  Single<List<ExpenseVO>> getExpenseList(String requestId) {
    final String TAG = "getExpenseList";
    JsonObject query = new JsonObject();

    if (logger.isDebugEnabled()) {
      logger.debug("[{}:{}] query\n{}", TAG, requestId, query.encodePrettily());
    }

    return mongoClient.rxFind(ExpenseConstants.COLLECTION_NAME, query).map(resultList -> resultList
        .stream().map(json -> ExpenseVO.fromJson(json)).collect(Collectors.toList()));
  }
}
