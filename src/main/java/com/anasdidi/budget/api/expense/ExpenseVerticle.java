package com.anasdidi.budget.api.expense;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.reactivex.Single;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.mongo.MongoClient;
import io.vertx.reactivex.ext.web.Router;

public class ExpenseVerticle extends AbstractVerticle {

  private static Logger logger = LogManager.getLogger(ExpenseVerticle.class);
  private final Router mainRouter;
  private final MongoClient mongoClient;

  public ExpenseVerticle(Router mainRouter, MongoClient mongoClient) {
    this.mainRouter = mainRouter;
    this.mongoClient = mongoClient;
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    Router router = Router.router(vertx);

    router.post("/").handler(routingContext -> {
      String requestId = routingContext.get("requestId");
      JsonObject requestBody = routingContext.getBodyAsJson();

      if (logger.isDebugEnabled()) {
        logger.debug("[debug] requestBody\n{}", requestBody.encodePrettily());
      }

      Single<JsonObject> responseSubscriber = Single.just(requestBody)//
          .map(json -> ExpenseVO.fromJson(json))//
          .flatMap(vo -> {
            logger.info("[debug] Save vo");
            return mongoClient.rxSave("expenses", ExpenseVO.toDocument(vo)).toSingle();
          }).map(id -> {
            logger.info("[debug] Save success");
            JsonObject responseBody = new JsonObject()//
                .put("status", new JsonObject()//
                    .put("isSuccess", true)//
                    .put("message", "Record successfully created."))//
                .put("data", new JsonObject()//
                    .put("requestId", requestId)//
                    .put("id", id));
            return responseBody;
          });

      responseSubscriber.subscribe(responseBody -> {
        routingContext.response().putHeader("Content-Type", "application/json").setStatusCode(201)
            .end(responseBody.encode());
      }, e -> {
        routingContext.response().putHeader("Content-Type", "application/json").setStatusCode(500)
            .end(new JsonObject().put("error", e.getMessage()).encode());
      });
    });

    mainRouter.mountSubRouter("/api/expense", router);
    logger.info("[start] Deployement success");
    startPromise.complete();
  }
}
