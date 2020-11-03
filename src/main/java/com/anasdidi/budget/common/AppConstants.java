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
}
