package com.anasdidi.budget.api.expense;

import com.anasdidi.budget.MainVerticle;
import com.anasdidi.budget.common.AppConfig;
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
public class TestExpenseVerticle {

  private String requestURI = "/budget/api/expense";

  private JsonObject generateDocument() {
    long prefix = System.currentTimeMillis();
    return new JsonObject()//
        .put("item", prefix + "item")//
        .put("price", 0);
  }

  private MongoClient getMongoClient(Vertx vertx) throws Exception {
    AppConfig appConfig = AppConfig.instance();
    return MongoClient.createShared(vertx, new JsonObject()//
        .put("host", appConfig.getMongoHost())//
        .put("port", appConfig.getMongoPort())//
        .put("username", appConfig.getMongoUsername())//
        .put("password", appConfig.getMongoPassword())//
        .put("authSource", appConfig.getMongoAuthSource())//
        .put("db_name", appConfig.getMongoDbName()));
  }

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle(),
        testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  void testExpenseCreateSuccess(Vertx vertx, VertxTestContext testContext) throws Exception {
    AppConfig appConfig = AppConfig.instance();
    WebClient webClient = WebClient.create(vertx);
    JsonObject expense = generateDocument();

    webClient.post(appConfig.getAppPort(), appConfig.getAppHost(), requestURI)
        .rxSendJsonObject(expense).subscribe(response -> {
          testContext.verify(() -> {
            Assertions.assertEquals(201, response.statusCode());
            Assertions.assertEquals("application/json", response.getHeader("Content-Type"));

            JsonObject responseBody = response.bodyAsJsonObject();
            Assertions.assertNotNull(responseBody);

            JsonObject status = responseBody.getJsonObject("status");
            Assertions.assertNotNull(status);
            Assertions.assertEquals(true, status.getBoolean("isSuccess"));
            Assertions.assertEquals("Record successfully created.", status.getString("message"));

            JsonObject data = responseBody.getJsonObject("data");
            Assertions.assertNotNull(data);
            Assertions.assertNotNull(data.getString("requestId"));
            Assertions.assertNotNull(data.getString("id"));

            testContext.completeNow();
          });
        }, e -> testContext.failNow(e));
  }

  @Test
  void testExpenseUpdateSuccess(Vertx vertx, VertxTestContext testContext) throws Exception {
    AppConfig appConfig = AppConfig.instance();
    WebClient webClient = WebClient.create(vertx);
    JsonObject document = generateDocument();
    MongoClient mongoClient = getMongoClient(vertx);

    mongoClient.rxInsert("expenses", document).subscribe(id -> {
      Assertions.assertNotNull(id);
      document.put("price", 999);
      document.put("version", 0);

      webClient.put(appConfig.getAppPort(), appConfig.getAppHost(), requestURI + "/" + id)
          .rxSendJsonObject(document).subscribe(response -> {
            testContext.verify(() -> {
              Assertions.assertEquals(200, response.statusCode());
              Assertions.assertEquals("application/json", response.getHeader("Content-Type"));

              JsonObject responseBody = response.bodyAsJsonObject();
              Assertions.assertNotNull(responseBody);

              JsonObject status = responseBody.getJsonObject("status");
              Assertions.assertNotNull(status);
              Assertions.assertEquals(true, status.getBoolean("isSuccess"));
              Assertions.assertEquals("Record successfully updated.", status.getString("message"));

              JsonObject data = responseBody.getJsonObject("data");
              Assertions.assertNotNull(data);
              Assertions.assertNotNull(data.getString("requestId"));
              Assertions.assertNotNull(data.getString("id"));

              testContext.completeNow();
            });
          }, e -> testContext.failNow(e));
    }, e -> testContext.failNow(e));
  }
}
