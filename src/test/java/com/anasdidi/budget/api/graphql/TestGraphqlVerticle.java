package com.anasdidi.budget.api.graphql;

import com.anasdidi.budget.MainVerticle;
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
import io.vertx.reactivex.ext.web.client.WebClient;

@ExtendWith(VertxExtension.class)
public class TestGraphqlVerticle {

  private String requestURI = AppConstants.CONTEXT_PATH + GraphqlConstants.REQUEST_URI;
  // payload = { "iss": "issuer" }, secret = secret
  private String accessToken =
      "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJpc3N1ZXIifQ.FgSmi1aRikqCuBD_FwCa6yla30DVc9AgnyF-HAII--U";

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle(),
        testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  void testGraphqlSuccess(Vertx vertx, VertxTestContext testContext) throws Exception {
    AppConfig appConfig = AppConfig.instance();
    WebClient webClient = WebClient.create(vertx);
    String testValue = "" + System.currentTimeMillis();
    JsonObject requestBody = new JsonObject()//
        .put("query", "query($value: String!) { ping(value: $value) { isSuccess testValue } }")//
        .put("variables", new JsonObject()//
            .put("value", testValue));

    Thread.sleep(500);
    webClient.post(appConfig.getAppPort(), appConfig.getAppHost(), requestURI)
        .putHeader("Authorization", "Bearer " + accessToken).rxSendJsonObject(requestBody)
        .subscribe(response -> {
          testContext.verify(() -> {
            Assertions.assertEquals(AppConstants.STATUS_CODE_OK, response.statusCode());
            Assertions.assertEquals(AppConstants.MEDIA_APP_JSON,
                response.getHeader("Content-Type"));

            JsonObject responseBody = response.bodyAsJsonObject();
            Assertions.assertNotNull(responseBody);

            JsonObject data = responseBody.getJsonObject("data");
            Assertions.assertNotNull(data);

            JsonObject ping = data.getJsonObject("ping");
            Assertions.assertNotNull(ping);
            Assertions.assertEquals(true, ping.getBoolean("isSuccess"));
            Assertions.assertEquals(testValue, ping.getString("testValue"));

            testContext.completeNow();
          });
        }, e -> testContext.failNow(e));
  }
}
