package com.muchq.holler.util;

import com.muchq.holler.core.Header;
import com.muchq.holler.core.HttpRequest;
import com.muchq.holler.core.HttpResponse;
import com.muchq.holler.core.QueryParam;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import org.asynchttpclient.Request;
import org.asynchttpclient.RequestBuilder;
import org.asynchttpclient.Response;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class RequestUtils {
  public static final String CONTENT_TYPE = "Content-Type";

  private RequestUtils() {}

  public static QueryParam param(String key, String val1, String... rest) {
    return QueryParam.builder()
        .setKey(key)
        .addValues(val1)
        .addValues(rest)
        .build();
  }

  public static Request toAsyncRequest(HttpRequest request) {
    return new RequestBuilder()
        .setBody(request.getBodyBytes())
        .setMethod(request.getMethod())
        .setQueryParams(toAsyncQueryParams(request.getQueryParams()))
        .setUrl(request.getUrl())
        .setHeaders(toAsyncHeaders(request))
        .build();
  }

  public static HttpResponse toHollerResponse(Response asyncResponse) {
    return HttpResponse.builder()
        .setBodyAsInputStream(asyncResponse.getResponseBodyAsStream())
        .setStatusCode(asyncResponse.getStatusCode())
        .setHeaders(toHollerHeaders(asyncResponse.getHeaders()))
        .build();
  }

  private static Map<String, List<String>> toAsyncQueryParams(List<QueryParam> hollerParams) {
    return hollerParams.stream()
        .collect(Collectors.toMap(QueryParam::getKey, QueryParam::getValues));
  }

  private static HttpHeaders toAsyncHeaders(HttpRequest request) {
    HttpHeaders httpHeaders = new DefaultHttpHeaders();
    for (Header header : request.getHeaders()) {
      httpHeaders.add(header.getKey(), header.getValue());
    }
    httpHeaders.remove(CONTENT_TYPE);
    httpHeaders.add(CONTENT_TYPE, request.getContentType());
    return httpHeaders;
  }

  private static List<Header> toHollerHeaders(HttpHeaders asyncHeaders) {
    return asyncHeaders.entries().stream()
        .map((e) -> Header.builder().setKey(e.getKey()).addValue(e.getValue()).build())
        .collect(Collectors.toList());
  }
}
