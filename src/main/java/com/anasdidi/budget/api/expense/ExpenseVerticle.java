package com.anasdidi.budget.api.expense;

import com.anasdidi.budget.common.AppConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.vertx.core.Promise;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.ext.auth.jwt.JWTAuth;
import io.vertx.reactivex.ext.mongo.MongoClient;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.JWTAuthHandler;

public class ExpenseVerticle extends AbstractVerticle {

  private static Logger logger = LogManager.getLogger(ExpenseVerticle.class);
  private final Router mainRouter;
  private final EventBus eventBus;
  private final JWTAuth jwtAuth;
  private final ExpenseService expenseService;
  private final ExpenseController expenseController;

  public ExpenseVerticle(Router mainRouter, EventBus eventBus, JWTAuth jwtAuth,
      MongoClient mongoClient) {
    this.mainRouter = mainRouter;
    this.eventBus = eventBus;
    this.jwtAuth = jwtAuth;
    this.expenseService = new ExpenseService(mongoClient);
    this.expenseController = new ExpenseController(expenseService);
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    Router router = Router.router(vertx);
    router.route().handler(JWTAuthHandler.create(jwtAuth));

    router.post("/").handler(expenseController::doCreate);
    router.put("/:id").handler(expenseController::doUpdate);
    router.delete("/:id").handler(expenseController::doDelete);

    eventBus.consumer(AppConstants.EVENT_GET_EXPENSE_BY_ID)
        .handler(expenseController::getExpenseById);
    eventBus.consumer(AppConstants.EVENT_GET_EXPENSE_LIST)
        .handler(expenseController::getExpenseList);

    mainRouter.mountSubRouter(ExpenseConstants.REQUEST_URI, router);
    logger.info("[start] Deployment success");
    startPromise.complete();
  }
}
