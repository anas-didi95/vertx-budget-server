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
        .put("MONGO_CONFIG", getMongoConfig())//
        .put("MONGO_USERNAME", getMongoUsername())//
        .put("MONGO_PASSWORD", getMongoPassword())//
        .put("MONGO_HOST", getMongoHost())//
        .put("MONGO_PORT", getMongoPort())//
        .put("MONGO_AUTH_SOURCE", getMongoAuthSource())//
        .put("MONGO_DB_NAME", getMongoDbName())//
        .put("GRAPHIQL_ENABLE", getGraphiqlEnable())//
        .put("JWT_SECRET", getJwtSecret())//
        .put("JWT_ISSUER", getJwtIssuer())//
        .put("JWT_EXPIRE_IN_MINUTES", getJwtExpireInMinutes())//
        .encodePrettily();
  }

  public Integer getAppPort() {
    return cfg.getInteger("APP_PORT");
  }

  public String getAppHost() {
    return cfg.getString("APP_HOST", "localhost");
  }

  public JsonObject getMongoConfig() {
    return new JsonObject()//
        .put("connection_string", cfg.getString("MONGO_CONNECTION_STRING"));
  }

  public String getMongoUsername() {
    return cfg.getString("MONGO_USERNAME");
  }

  public String getMongoPassword() {
    return cfg.getString("MONGO_PASSWORD");
  }

  public String getMongoHost() {
    return cfg.getString("MONGO_HOST");
  }

  public Integer getMongoPort() {
    return cfg.getInteger("MONGO_PORT");
  }

  public String getMongoAuthSource() {
    return cfg.getString("MONGO_AUTH_SOURCE", "admin");
  }

  public String getMongoDbName() {
    return cfg.getString("MONGO_DB_NAME", "budget");
  }

  public boolean getGraphiqlEnable() {
    return cfg.getBoolean("GRAPHIQL_ENABLE", false);
  }

  public String getJwtSecret() {
    return cfg.getString("JWT_SECRET");
  }

  public String getJwtIssuer() {
    return cfg.getString("JWT_ISSUER");
  }

  public int getJwtExpireInMinutes() {
    return cfg.getInteger("JWT_EXPIRE_IN_MINUTES");
  }
}
