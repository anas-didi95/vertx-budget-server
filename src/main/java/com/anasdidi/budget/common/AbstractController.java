package com.anasdidi.budget.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.RoutingContext;

public abstract class AbstractController {

  private static final Logger logger = LogManager.getLogger(AbstractController.class);

  public void sendResponse(RoutingContext routingContext, int statusCode,
      Single<JsonObject> subscriber, String requestId) {
    final String TAG = "sendResponse";

    subscriber.subscribe(responseBody -> {
      logger.info("[{}:{}] statusCode={}", TAG, requestId, statusCode);
      if (logger.isDebugEnabled()) {
        logger.debug("[{}:{}] responseBody\n{}", TAG, requestId, responseBody.encodePrettily());
      }

      routingContext.response().headers().addAll(AppConstants.HEADERS);
      routingContext.response()//
          .setStatusCode(201)//
          .end(responseBody.encode());
    });
  }
}
