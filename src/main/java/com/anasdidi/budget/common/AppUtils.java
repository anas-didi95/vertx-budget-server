package com.anasdidi.budget.common;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public class AppUtils {

  public static String getFormattedMillis(long millis) {
    long dayToMs = 1000 * 60 * 60 * 24;
    long hourToMs = 1000 * 60 * 60;
    long minToMs = 1000 * 60;
    long secToMs = 1000;

    long day = millis / dayToMs;
    millis %= dayToMs;

    long hour = millis / hourToMs;
    millis %= hourToMs;

    long min = millis / minToMs;
    millis %= minToMs;

    long sec = millis / secToMs;
    millis %= secToMs;

    return String.format("%dd %dh %dmin %dsec", day, hour, min, sec);
  }

  public static String generateUUID() {
    return UUID.randomUUID().toString().replace("-", "").toUpperCase();
  }

  public static String getFormattedDateString(Instant instant, String format) {
    Date date = Date.from(instant);
    SimpleDateFormat sdf = new SimpleDateFormat(format);

    return sdf.format(date);
  }
}
