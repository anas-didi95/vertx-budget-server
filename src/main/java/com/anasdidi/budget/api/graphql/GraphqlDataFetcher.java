package com.anasdidi.budget.api.graphql;

import com.anasdidi.budget.api.graphql.dto.ExpenseDTO;
import com.anasdidi.budget.common.AppUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import graphql.schema.DataFetchingEnvironment;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.EventBus;

class GraphqlDataFetcher {

  private static final Logger logger = LogManager.getLogger(GraphqlDataFetcher.class);
  private final EventBus eventBux;

  GraphqlDataFetcher(EventBus eventBus) {
    this.eventBux = eventBus;
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
      eventBux.rxRequest("/expense/id", requestBody).subscribe(response -> {
        JsonObject responseBody = (JsonObject) response.body();

        if (logger.isDebugEnabled()) {
          logger.debug("[{}:{}] responseBody\n{}", TAG, requestId, responseBody.encodePrettily());
        }

        ExpenseDTO result = ExpenseDTO.fromJson(responseBody);
        promise.complete(result);
      }, e -> promise.fail(e));
    }
  }
}
