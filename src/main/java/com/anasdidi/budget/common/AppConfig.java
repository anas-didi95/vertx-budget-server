package com.anasdidi.budget.common;

import io.reactivex.annotations.NonNull;
import io.vertx.core.json.JsonObject;

public class AppConfig {

  private static AppConfig appConfig;
  private final JsonObject cfg;

  public static AppConfig create(@NonNull JsonObject cfg) {
    appConfig = new AppConfig(cfg);
    return appConfig;
  }

  public static AppConfig instance() throws Exception {
    if (appConfig == null) {
      throw new Exception("AppConfig is null!");
    }
    return appConfig;
  }

  private AppConfig(JsonObject cfg) {
    this.cfg = cfg;
  }

  @Override
  public String toString() {
    return new JsonObject()//
        .put("APP_PORT", getAppPort())//
        .put("APP_HOST", getAppHost())//
        .encodePrettily();
  }

  public Integer getAppPort() {
    return cfg.getInteger("APP_PORT");
  }

  public String getAppHost() {
    return cfg.getString("APP_HOST");
  }
}
