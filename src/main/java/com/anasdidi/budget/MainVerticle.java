package com.anasdidi.budget;

import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.config.ConfigRetriever;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.web.Router;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    ConfigRetriever configRetriever = ConfigRetriever.create(vertx,
        new ConfigRetrieverOptions().addStore(new ConfigStoreOptions().setType("env")));

    configRetriever.rxGetConfig().subscribe(cfg -> {
      Router router = Router.router(vertx);
      router.get("/").handler(routingContext -> {
        routingContext.response().setStatusCode(200).putHeader("Content-Type", "application/json")
            .end(new JsonObject().put("data", "Hello world").encode());
      });

      vertx.createHttpServer().requestHandler(router).rxListen(cfg.getInteger("APP_PORT"), cfg.getString("APP_HOST"))
          .subscribe(r -> startPromise.complete(), e -> startPromise.fail(e));
    }, e -> startPromise.fail(e));
  }
}
