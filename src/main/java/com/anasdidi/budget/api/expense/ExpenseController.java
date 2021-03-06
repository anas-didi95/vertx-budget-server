package com.anasdidi.budget.api.expense;

import java.util.stream.Collectors;
import com.anasdidi.budget.common.AbstractController;
import com.anasdidi.budget.common.AppConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.Message;
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

    Single<JsonObject> subscriber = Single.just(requestBody)//
        .map(json -> json.put("id", expenseId))//
        .map(json -> ExpenseVO.fromJson(json))//
        .flatMap(vo -> expenseService.delete(vo, requestId))//
        .map(id -> new JsonObject()//
            .put("status", new JsonObject()//
                .put("isSuccess", true)//
                .put("message", AppConstants.MSG_DELETE_SUCCESS))//
            .put("data", new JsonObject()//
                .put("requestId", requestId)));

    sendResponse(routingContext, AppConstants.STATUS_CODE_OK, subscriber, requestId);
  }

  void getExpenseById(Message<Object> request) {
    final String TAG = "getUserById";
    JsonObject requestBody = (JsonObject) request.body();
    String requestId = requestBody.getString("requestId");

    logger.info("[{}:{}] Get expense by id={}", TAG, requestId, requestBody.getString("id"));

    Single.just(requestBody)//
        .map(json -> ExpenseVO.fromJson(json))//
        .flatMap(vo -> expenseService.getExpenseById(vo, requestId))//
        .subscribe(vo -> request.reply(ExpenseVO.toJson(vo)));
  }

  void getExpenseList(Message<Object> request) {
    final String TAG = "getExpenseList";
    JsonObject requestBody = (JsonObject) request.body();
    String requestId = requestBody.getString("requestId");

    logger.info("[{}:{}] Get expense list", TAG, requestId);

    Single.just(requestBody)//
        .flatMap(json -> expenseService.getExpenseList(requestId))//
        .subscribe(resultList -> {
          JsonArray responseBody = new JsonArray(
              resultList.stream().map(vo -> ExpenseVO.toJson(vo)).collect(Collectors.toList()));
          request.reply(responseBody);
        });
  }
}
