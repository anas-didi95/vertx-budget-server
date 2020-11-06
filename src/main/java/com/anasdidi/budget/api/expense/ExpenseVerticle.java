package com.anasdidi.budget.api.expense;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.ext.mongo.MongoClient;
import io.vertx.reactivex.ext.web.Router;

public class ExpenseVerticle extends AbstractVerticle {

  private static Logger logger = LogManager.getLogger(ExpenseVerticle.class);
  private final Router mainRouter;
  private final EventBus eventBus;
  private final ExpenseService expenseService;
  private final ExpenseController expenseController;

  public ExpenseVerticle(Router mainRouter, EventBus eventBus, MongoClient mongoClient) {
    this.mainRouter = mainRouter;
    this.eventBus = eventBus;
    this.expenseService = new ExpenseService(mongoClient);
    this.expenseController = new ExpenseController(expenseService);
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    Router router = Router.router(vertx);

    router.post("/").handler(expenseController::doCreate);
    router.put("/:id").handler(expenseController::doUpdate);
    router.delete("/:id").handler(expenseController::doDelete);

    eventBus.consumer("/expense/id").handler(request -> {
      // JsonObject requestBody = (JsonObject) request.body();
      JsonObject responseBody = new JsonObject()//
          .put("_id", "id")//
          .put("item", "item")//
          .put("price", 99.99)//
          .put("createDate", "createDate")//
          .put("updateDate", "updateDate")//
          .put("version", 0);

      request.reply(responseBody);
    });

    mainRouter.mountSubRouter(ExpenseConstants.REQUEST_URI, router);
    logger.info("[start] Deployment success");
    startPromise.complete();
  }
}
