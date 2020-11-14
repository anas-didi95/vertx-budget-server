package com.anasdidi.budget;

import java.util.HashSet;
import java.util.Set;
import com.anasdidi.budget.api.expense.ExpenseVerticle;
import com.anasdidi.budget.api.graphql.GraphqlVerticle;
import com.anasdidi.budget.common.AppConfig;
import com.anasdidi.budget.common.AppConstants;
import com.anasdidi.budget.common.AppUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Log4j2LogDelegateFactory;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.healthchecks.Status;
import io.vertx.reactivex.config.ConfigRetriever;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.ext.auth.jwt.JWTAuth;
import io.vertx.reactivex.ext.healthchecks.HealthCheckHandler;
import io.vertx.reactivex.ext.mongo.MongoClient;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.ext.web.handler.CorsHandler;

public class MainVerticle extends AbstractVerticle {

  private static final Logger logger = LogManager.getLogger(MainVerticle.class);
  private static final long serverStartTime = System.currentTimeMillis();

  public MainVerticle() {
    System.setProperty("vertx.logger-delegate-factory-class-name",
        Log4j2LogDelegateFactory.class.getName());
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    ConfigRetriever configRetriever = ConfigRetriever.create(vertx,
        new ConfigRetrieverOptions().addStore(new ConfigStoreOptions().setType("env")));

    configRetriever.rxGetConfig().subscribe(cfg -> {
      AppConfig appConfig = AppConfig.create(cfg);
      logger.info("[start] appConfig\n{}", appConfig.toString());

      MongoClient mongoClient = MongoClient.createShared(vertx, new JsonObject()//
          .put("host", appConfig.getMongoHost())//
          .put("port", appConfig.getMongoPort())//
          .put("username", appConfig.getMongoUsername())//
          .put("password", appConfig.getMongoPassword())//
          .put("authSource", appConfig.getMongoAuthSource())//
          .put("db_name", appConfig.getMongoDbName()));

      Router router = Router.router(vertx);
      router.route().handler(setupCorsHandler());
      router.route().handler(setupBodyHandler());
      router.route().handler(
          routingContext -> routingContext.put("requestId", AppUtils.generateUUID()).next());
      router.get("/ping").handler(setupHealthCheck());

      @SuppressWarnings("deprecation")
      JWTAuth jwtAuth = JWTAuth.create(vertx, new JWTAuthOptions()//
          .setJWTOptions(new JWTOptions()//
              .setIssuer(appConfig.getJwtIssuer())//
              .setExpiresInMinutes(appConfig.getJwtExpireInMinutes()))//
          .addPubSecKey(new PubSecKeyOptions()//
              .setAlgorithm("HS256")//
              .setPublicKey(appConfig.getJwtSecret())//
              .setSymmetric(true)));

      EventBus eventBus = vertx.eventBus();
      vertx.deployVerticle(new GraphqlVerticle(router, eventBus, jwtAuth));
      vertx.deployVerticle(new ExpenseVerticle(router, eventBus, jwtAuth, mongoClient));

      int port = appConfig.getAppPort();
      String host = appConfig.getAppHost();
      Router contextPath = Router.router(vertx).mountSubRouter(AppConstants.CONTEXT_PATH, router);
      vertx.createHttpServer().requestHandler(contextPath).rxListen(port, host).subscribe(r -> {
        logger.info("[start] HTTP server started on {}:{}", host, port);
        startPromise.complete();
      }, e -> startPromise.fail(e));
    }, e -> startPromise.fail(e));
  }

  private CorsHandler setupCorsHandler() {
    Set<String> headerNames = new HashSet<>();
    headerNames.add("Accept");
    headerNames.add("Content-Type");

    Set<HttpMethod> methods = new HashSet<>();
    methods.add(HttpMethod.GET);
    methods.add(HttpMethod.POST);
    methods.add(HttpMethod.PUT);
    methods.add(HttpMethod.DELETE);

    return CorsHandler.create("*")//
        .allowedHeaders(headerNames)//
        .allowedMethods(methods);
  }

  private BodyHandler setupBodyHandler() {
    BodyHandler bodyHandler = BodyHandler.create();
    return bodyHandler;
  }

  private HealthCheckHandler setupHealthCheck() {
    HealthCheckHandler handler = HealthCheckHandler.create(vertx);

    handler.register("uptime", promise -> {
      long uptime = System.currentTimeMillis() - serverStartTime;
      promise.complete(Status.OK(new JsonObject().put("startTime", serverStartTime)
          .put("uptime", uptime).put("formatted", AppUtils.getFormattedMillis(uptime))));
    });

    return handler;
  }
}
