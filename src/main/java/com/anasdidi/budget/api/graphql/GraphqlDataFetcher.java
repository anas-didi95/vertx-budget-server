package com.anasdidi.budget.api.graphql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.anasdidi.budget.api.graphql.dto.ExpenseDTO;
import com.anasdidi.budget.common.AppConstants;
import com.anasdidi.budget.common.AppUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import graphql.schema.DataFetchingEnvironment;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.EventBus;

class GraphqlDataFetcher {

  private static final Logger logger = LogManager.getLogger(GraphqlDataFetcher.class);
  private final EventBus eventBux;

  GraphqlDataFetcher(EventBus eventBus) {
    this.eventBux = eventBus;
  }

  void ping(DataFetchingEnvironment environment, Promise<Map<String, Object>> promise) {
    final String TAG = "expense";
    String requestId = AppUtils.generateUUID();
    String value = environment.getArgument("value");

    if (logger.isDebugEnabled()) {
      logger.debug("[{}:{}] value={}", TAG, requestId, value);
    }

    Map<String, Object> result = new HashMap<>();
    result.put("isSuccess", true);
    result.put("testValue", value);

    promise.complete(result);
  }

  void expense(DataFetchingEnvironment environment, Promise<ExpenseDTO> promise) {
    final String TAG = "expense";
    String requestId = AppUtils.generateUUID();
    String id = environment.getArgumentOrDefault("id", "");
    JsonObject requestBody = new JsonObject()//
        .put("requestId", requestId)//
        .put("id", id);

    if (logger.isDebugEnabled()) {
      logger.debug("[{}:{}] requestBody\n{}", TAG, requestId, requestBody.encodePrettily());
    }

    if (!id.isBlank()) {
      logger.info("[{}:{}] event={}", TAG, requestId, AppConstants.EVENT_GET_EXPENSE_BY_ID);
      eventBux.rxRequest(AppConstants.EVENT_GET_EXPENSE_BY_ID, requestBody).subscribe(response -> {
        JsonObject responseBody = (JsonObject) response.body();

        if (logger.isDebugEnabled()) {
          logger.debug("[{}:{}] responseBody\n{}", TAG, requestId, responseBody.encodePrettily());
        }

        ExpenseDTO result = ExpenseDTO.fromJson(responseBody);
        promise.complete(result);
      }, e -> promise.fail(e));
    }
  }

  void expenses(DataFetchingEnvironment environment, Promise<List<ExpenseDTO>> promise) {
    final String TAG = "expenses";
    String requestId = AppUtils.generateUUID();
    JsonObject requestBody = new JsonObject()//
        .put("requestId", requestId);

    if (logger.isDebugEnabled()) {
      logger.debug("[{}:{}] requestBody\n{}", TAG, requestId, requestBody.encodePrettily());
    }

    logger.info("[{}:{}] event={}", TAG, requestId, AppConstants.EVENT_GET_EXPENSE_LIST);
    eventBux.rxRequest(AppConstants.EVENT_GET_EXPENSE_LIST, requestBody).subscribe(response -> {
      JsonArray responseBody = (JsonArray) response.body();

      List<ExpenseDTO> resultList = responseBody.stream()
          .map(json -> ExpenseDTO.fromJson((JsonObject) json)).collect(Collectors.toList());
      promise.complete(resultList);
    }, e -> promise.fail(e));
  }
}
