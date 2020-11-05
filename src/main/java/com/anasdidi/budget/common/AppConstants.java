package com.anasdidi.budget.common;

import java.util.HashMap;
import java.util.Map;

public class AppConstants {

  public static final Map<String, String> HEADERS = new HashMap<>();

  static {
    HEADERS.put("Content-Type", "application/json");
    HEADERS.put("Cache-Control", "no-store, no-cache");
    HEADERS.put("X-Content-Type-Options", "nosniff");
    HEADERS.put("X-XSS-Protection", "1; mode=block");
    HEADERS.put("X-Frame-Options", "deny");
  }

  public static final String MEDIA_APP_JSON = "application/json";

  public static final int STATUS_CODE_OK = 200;
  public static final int STATUS_CODE_CREATED = 201;

  public static final String MSG_CREATE_SUCCESS = "Record successfully created.";
  public static final String MSG_UPDATE_SUCCESS = "Record successfully updated.";
  public static final String MSG_DELETE_SUCCESS = "Record successfully deleted.";
}
