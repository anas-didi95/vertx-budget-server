package com.anasdidi.budget.api.expense;

import com.anasdidi.budget.MainVerticle;
import com.anasdidi.budget.common.AppConfig;
import com.anasdidi.budget.common.AppConstants;
import org.junit.jupiter.api.AfterAll;
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
        .put("price", 0)//
        .put("version", 0);
  }

  private static MongoClient getMongoClient(Vertx vertx) throws Exception {
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

  @AfterAll
  static void postTesting(Vertx vertx, VertxTestContext testContext) throws Exception {
    MongoClient mongoClient = getMongoClient(vertx);

    mongoClient.rxRemoveDocuments("expenses", new JsonObject()).subscribe(result -> {
      testContext.verify(() -> {
        testContext.completeNow();
      });
    }, e -> testContext.failNow(e));
  }

  @Test
  void testExpenseCreateSuccess(Vertx vertx, VertxTestContext testContext) throws Exception {
    AppConfig appConfig = AppConfig.instance();
    WebClient webClient = WebClient.create(vertx);
    JsonObject expense = generateDocument();
    MongoClient mongoClient = getMongoClient(vertx);

    mongoClient.rxCount("expenses", new JsonObject()).subscribe(prevCount -> {
      webClient.post(appConfig.getAppPort(), appConfig.getAppHost(), requestURI)
          .rxSendJsonObject(expense).subscribe(response -> {
            testContext.verify(() -> {
              Assertions.assertEquals(AppConstants.STATUS_CODE_CREATED, response.statusCode());
              Assertions.assertEquals(AppConstants.MEDIA_APP_JSON,
                  response.getHeader("Content-Type"));

              JsonObject responseBody = response.bodyAsJsonObject();
              Assertions.assertNotNull(responseBody);

              JsonObject status = responseBody.getJsonObject("status");
              Assertions.assertNotNull(status);
              Assertions.assertEquals(true, status.getBoolean("isSuccess"));
              Assertions.assertEquals(AppConstants.MSG_CREATE_SUCCESS, status.getString("message"));

              JsonObject data = responseBody.getJsonObject("data");
              Assertions.assertNotNull(data);
              Assertions.assertNotNull(data.getString("requestId"));
              Assertions.assertNotNull(data.getString("id"));

              mongoClient.rxCount("expenses", new JsonObject()).subscribe(currCount -> {
                testContext.verify(() -> {
                  Assertions.assertNotEquals(prevCount, currCount);

                  testContext.completeNow();
                });
              }, e -> testContext.failNow(e));
            });
          }, e -> testContext.failNow(e));
    }, e -> testContext.failNow(e));
  }

  @Test
  void testExpenseUpdateSuccess(Vertx vertx, VertxTestContext testContext) throws Exception {
    AppConfig appConfig = AppConfig.instance();
    WebClient webClient = WebClient.create(vertx);
    JsonObject document = generateDocument();
    MongoClient mongoClient = getMongoClient(vertx);

    mongoClient.rxInsert("expenses", document).subscribe(id -> {
      document.put("price", 999);
      document.put("version", 0);

      webClient.put(appConfig.getAppPort(), appConfig.getAppHost(), requestURI + "/" + id)
          .rxSendJsonObject(document).subscribe(response -> {
            testContext.verify(() -> {
              Assertions.assertEquals(AppConstants.STATUS_CODE_OK, response.statusCode());
              Assertions.assertEquals(AppConstants.MEDIA_APP_JSON,
                  response.getHeader("Content-Type"));

              JsonObject responseBody = response.bodyAsJsonObject();
              Assertions.assertNotNull(responseBody);

              JsonObject status = responseBody.getJsonObject("status");
              Assertions.assertNotNull(status);
              Assertions.assertEquals(true, status.getBoolean("isSuccess"));
              Assertions.assertEquals(AppConstants.MSG_UPDATE_SUCCESS, status.getString("message"));

              JsonObject data = responseBody.getJsonObject("data");
              Assertions.assertNotNull(data);
              Assertions.assertNotNull(data.getString("requestId"));
              Assertions.assertNotNull(data.getString("id"));

              mongoClient.rxFindOne("expenses", new JsonObject().put("_id", id), new JsonObject())
                  .subscribe(currDoc -> {
                    testContext.verify(() -> {
                      Assertions.assertNotEquals(currDoc.getLong("version"),
                          document.getLong("version"));

                      testContext.completeNow();
                    });
                  }, e -> testContext.failNow(e));
              testContext.completeNow();
            });
          }, e -> testContext.failNow(e));
    }, e -> testContext.failNow(e));
  }

  @Test
  void testExpenseDeleteSuccess(Vertx vertx, VertxTestContext testContext) throws Exception {
    AppConfig appConfig = AppConfig.instance();
    WebClient webClient = WebClient.create(vertx);
    JsonObject document = generateDocument();
    MongoClient mongoClient = getMongoClient(vertx);

    mongoClient.rxInsert("expenses", document).subscribe(id -> {
      mongoClient.rxCount("expenses", new JsonObject()).subscribe(prevCount -> {
        JsonObject body = new JsonObject().put("version", 0);

        webClient.delete(appConfig.getAppPort(), appConfig.getAppHost(), requestURI + "/" + id)
            .rxSendJsonObject(body).subscribe(response -> {
              testContext.verify(() -> {
                Assertions.assertEquals(AppConstants.STATUS_CODE_OK, response.statusCode());
                Assertions.assertEquals(AppConstants.MEDIA_APP_JSON,
                    response.getHeader("Content-Type"));

                JsonObject responseBody = response.bodyAsJsonObject();
                Assertions.assertNotNull(responseBody);

                JsonObject status = responseBody.getJsonObject("status");
                Assertions.assertNotNull(status);
                Assertions.assertEquals(true, status.getBoolean("isSuccess"));
                Assertions.assertEquals(AppConstants.MSG_DELETE_SUCCESS,
                    status.getString("message"));

                JsonObject data = responseBody.getJsonObject("data");
                Assertions.assertNotNull(data);
                Assertions.assertNotNull(data.getString("requestId"));

                mongoClient.rxCount("expenses", new JsonObject()).subscribe(currCount -> {
                  testContext.verify(() -> {
                    Assertions.assertNotEquals(prevCount, currCount);

                    testContext.completeNow();
                  });
                }, e -> testContext.failNow(e));
              });
            }, e -> testContext.failNow(e));
      }, e -> testContext.failNow(e));
    }, e -> testContext.failNow(e));
  }
}
