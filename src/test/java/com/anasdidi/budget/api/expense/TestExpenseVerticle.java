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
import io.vertx.reactivex.ext.web.client.WebClient;

@ExtendWith(VertxExtension.class)
public class TestExpenseVerticle {

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle(),
        testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  void testExpenseCreateSuccess(Vertx vertx, VertxTestContext testContext) throws Exception {
    AppConfig appConfig = AppConfig.instance();
    WebClient webClient = WebClient.create(vertx);

    webClient.post(appConfig.getAppPort(), appConfig.getAppHost(), "/budget/api/expense").rxSend()
        .subscribe(response -> {
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

            testContext.completeNow();
          });
        }, e -> testContext.failNow(e));
  }
}
