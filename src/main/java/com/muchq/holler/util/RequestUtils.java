package com.muchq.holler.util;

import com.muchq.holler.core.QueryParam;

public final class RequestUtils {
  private RequestUtils() {

  }

  public static QueryParam param(String key, String val1, String... rest) {
    return QueryParam.builder()
        .setKey(key)
        .addValues(val1)
        .addValues(rest)
        .build();
  }
}
