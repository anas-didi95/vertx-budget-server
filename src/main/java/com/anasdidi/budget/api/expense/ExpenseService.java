package com.anasdidi.budget.api.expense;

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
    JsonObject document = ExpenseVO.toDocument(vo);

    if (logger.isDebugEnabled()) {
      logger.debug("[{}:{}] document\n{}", TAG, requestId, document.encodePrettily());
    }

    return mongoClient.rxSave("expenses", document).toSingle();
  }
}
