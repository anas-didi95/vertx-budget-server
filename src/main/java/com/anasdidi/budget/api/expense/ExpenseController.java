package com.anasdidi.budget.api.expense;

import com.anasdidi.budget.common.AbstractController;
import com.anasdidi.budget.common.AppConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.RoutingContext;

class ExpenseController extends AbstractController {

  private static final Logger logger = LogManager.getLogger(ExpenseController.class);
  private final ExpenseService expenseService;

  ExpenseController(ExpenseService expenseService) {
    this.expenseService = expenseService;
  }

  void doCreate(RoutingContext routingContext) {
    final String TAG = "doCreate";
    String requestId = routingContext.get("requestId");
    JsonObject requestBody = routingContext.getBodyAsJson();

    if (logger.isDebugEnabled()) {
      logger.debug("[{}:{}] requestBody\n{}", TAG, requestId, requestBody.encodePrettily());
    }

    Single<JsonObject> subscriber = Single.just(requestBody)//
        .map(json -> ExpenseVO.fromJson(json))//
        .flatMap(vo -> expenseService.create(vo, requestId))//
        .map(id -> new JsonObject()//
            .put("status", new JsonObject()//
                .put("isSuccess", true)//
                .put("message", AppConstants.MSG_CREATE_SUCCESS))
            .put("data", new JsonObject()//
                .put("requestId", requestId)//
                .put("id", id)));

    sendResponse(routingContext, AppConstants.STATUS_CODE_CREATED, subscriber, requestId);
  }

  void doUpdate(RoutingContext routingContext) {
    final String TAG = "doUpdate";
    String requestId = routingContext.get("requestId");
    String expenseId = routingContext.request().getParam("id");
    JsonObject requestBody = routingContext.getBodyAsJson();

    if (logger.isDebugEnabled()) {
      logger.debug("[{}:{}] expenseId={}", TAG, requestId, expenseId);
      logger.debug("[{}:{}] requestBody\n{}", TAG, requestId, requestBody.encodePrettily());
    }

    Single<JsonObject> subscriber = Single.just(requestBody)//
        .map(json -> json.put("id", expenseId))//
        .map(json -> ExpenseVO.fromJson(json))//
        .flatMap(vo -> expenseService.update(vo, requestId))//
        .map(id -> new JsonObject()//
            .put("status", new JsonObject()//
                .put("isSuccess", true)//
                .put("message", AppConstants.MSG_UPDATE_SUCCESS))//
            .put("data", new JsonObject()//
                .put("requestId", requestId)//
                .put("id", id)));

    sendResponse(routingContext, AppConstants.STATUS_CODE_OK, subscriber, requestId);
  }

  void doDelete(RoutingContext routingContext) {
    final String TAG = "doDelete";
    String requestId = routingContext.get("requestId");
    String expenseId = routingContext.request().getParam("id");
    JsonObject requestBody = routingContext.getBodyAsJson();

    if (logger.isDebugEnabled()) {
      logger.debug("[{}:{}] expenseId={}", TAG, requestId, expenseId);
      logger.debug("[{}:{}] requestBody\n{}", TAG, requestId, requestBody.encodePrettily());
    }

    Single<JsonObject> subscriber = Single.just("")//
        .map(s -> new JsonObject());

    sendResponse(routingContext, AppConstants.STATUS_CODE_OK, subscriber, requestId);
  }
}
