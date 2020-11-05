package com.anasdidi.budget.api.expense;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.vertx.core.Promise;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.mongo.MongoClient;
import io.vertx.reactivex.ext.web.Router;

public class ExpenseVerticle extends AbstractVerticle {

  private static Logger logger = LogManager.getLogger(ExpenseVerticle.class);
  private final Router mainRouter;
  private final ExpenseService expenseService;
  private final ExpenseController expenseController;

  public ExpenseVerticle(Router mainRouter, MongoClient mongoClient) {
    this.mainRouter = mainRouter;
    this.expenseService = new ExpenseService(mongoClient);
    this.expenseController = new ExpenseController(expenseService);
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    Router router = Router.router(vertx);

    router.post("/").handler(expenseController::doCreate);
    router.put("/:id").handler(expenseController::doUpdate);

    mainRouter.mountSubRouter("/api/expense", router);
    logger.info("[start] Deployment success");
    startPromise.complete();
  }
}
