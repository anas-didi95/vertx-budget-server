package com.anasdidi.budget.api.expense;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.web.Router;

public class ExpenseVerticle extends AbstractVerticle {

  private static Logger logger = LogManager.getLogger(ExpenseVerticle.class);
  private final Router mainRouter;

  public ExpenseVerticle(Router mainRouter) {
    this.mainRouter = mainRouter;
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    Router router = Router.router(vertx);

    router.post("/").handler(routingContext -> {
      String requestId = routingContext.get("requestId");
      JsonObject responseBody = new JsonObject()
          .put("status",
              new JsonObject().put("isSuccess", true).put("message",
                  "Record successfully created."))
          .put("data", new JsonObject().put("requestId", requestId));

      routingContext.response().putHeader("Content-Type", "application/json").setStatusCode(201)
          .end(responseBody.encode());
    });

    mainRouter.mountSubRouter("/api/expense", router);
    logger.info("[start] Deployement success");
    startPromise.complete();
  }
}
