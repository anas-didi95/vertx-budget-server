package com.anasdidi.budget;

import com.anasdidi.budget.common.AppConfig;
import com.anasdidi.budget.common.AppConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.mongo.MongoClient;
import io.vertx.reactivex.ext.web.client.WebClient;

@ExtendWith(VertxExtension.class)
public class TestMainVerticle {

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle(),
        testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  void verticle_deployed(Vertx vertx, VertxTestContext testContext) throws Throwable {
    testContext.completeNow();
  }

  @Test
  void testEnvironmentConfig(Vertx vertx, VertxTestContext testContext) throws Exception {
    AppConfig appConfig = AppConfig.instance();

    testContext.verify(() -> {
      Assertions.assertNotNull(appConfig.getAppPort());
      Assertions.assertNotNull(appConfig.getAppHost());
      Assertions.assertNotNull(appConfig.getMongoConfig());
      Assertions.assertNotNull(appConfig.getMongoHost());
      Assertions.assertNotNull(appConfig.getMongoPort());
      Assertions.assertNotNull(appConfig.getMongoUsername());
      Assertions.assertNotNull(appConfig.getMongoPassword());
      Assertions.assertNotNull(appConfig.getMongoAuthSource());
      Assertions.assertNotNull(appConfig.getMongoDbName());
      Assertions.assertNotNull(appConfig.getGraphiqlEnable());
      Assertions.assertNotNull(appConfig.getJwtSecret());
      Assertions.assertNotNull(appConfig.getJwtIssuer());
      Assertions.assertNotNull(appConfig.getJwtExpireInMinutes());

      testContext.completeNow();
    });
  }

  @Test
  void testPingSuccess(Vertx vertx, VertxTestContext testContext) throws Exception {
    WebClient webClient = WebClient.create(vertx);
    AppConfig appConfig = AppConfig.instance();

    webClient
        .get(appConfig.getAppPort(), appConfig.getAppHost(), AppConstants.CONTEXT_PATH + "/ping")
        .rxSend().subscribe(response -> {
          testContext.verify(() -> {
            Assertions.assertEquals(200, response.statusCode());

            JsonObject responseBody = response.bodyAsJsonObject();
            Assertions.assertNotNull(responseBody);
            Assertions.assertEquals("UP", responseBody.getString("outcome"));

            testContext.completeNow();
          });
        }, e -> testContext.failNow(e));
  }

  @Test
  void testMongoConnectionSuccess(Vertx vertx, VertxTestContext testContext) throws Exception {
    AppConfig appConfig = AppConfig.instance();
    MongoClient mongoClient = MongoClient.createShared(vertx, new JsonObject()//
        .put("host", appConfig.getMongoHost())//
        .put("port", appConfig.getMongoPort())//
        .put("username", appConfig.getMongoUsername())//
        .put("password", appConfig.getMongoPassword())//
        .put("authSource", appConfig.getMongoAuthSource())//
        .put("db_name", appConfig.getMongoDbName()));

    mongoClient.rxGetCollections().subscribe(resultList -> {
      testContext.verify(() -> {
        Assertions.assertNotNull(resultList);

        testContext.completeNow();
      });
    }, e -> testContext.failNow(e));
  }
}
