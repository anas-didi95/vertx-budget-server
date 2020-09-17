package com.anasdidi.budget;

import com.anasdidi.budget.common.AppConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Log4j2LogDelegateFactory;
import io.vertx.reactivex.config.ConfigRetriever;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.web.Router;

public class MainVerticle extends AbstractVerticle {

  private static final Logger logger = LogManager.getLogger(MainVerticle.class);

  public MainVerticle() {
    System.setProperty("vertx.logger-delegate-factory-class-name", Log4j2LogDelegateFactory.class.getName());
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    ConfigRetriever configRetriever = ConfigRetriever.create(vertx,
        new ConfigRetrieverOptions().addStore(new ConfigStoreOptions().setType("env")));

    configRetriever.rxGetConfig().subscribe(cfg -> {
      AppConfig appConfig = AppConfig.create(cfg);
      logger.info("appConfig\n{}", appConfig.toString());

      Router router = Router.router(vertx);
      router.get("/").handler(routingContext -> {
        routingContext.response().setStatusCode(200).putHeader("Content-Type", "application/json")
            .end(new JsonObject().put("data", "Hello world").encode());
      });

      vertx.createHttpServer().requestHandler(router).rxListen(appConfig.getAppPort(), appConfig.getAppHost())
          .subscribe(r -> startPromise.complete(), e -> startPromise.fail(e));
    }, e -> startPromise.fail(e));
  }
}
