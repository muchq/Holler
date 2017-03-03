package com.muchq.holler;

import com.muchq.holler.core.Header;
import com.muchq.holler.core.HttpRequest;
import com.muchq.holler.core.HttpResponse;
import com.muchq.holler.core.QueryParam;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Request;
import org.asynchttpclient.RequestBuilder;
import org.asynchttpclient.Response;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.muchq.holler.core.HeaderIF.CONTENT_TYPE;

public class AsyncHttp implements HttpClient {
  private final AsyncHttpClient async;

  public AsyncHttp(AsyncHttpClient async) {
    this.async = async;
  }

  @Override
  public CompletableFuture<HttpResponse> execute(HttpRequest request) {
    return async.executeRequest(convertRequest(request))
        .toCompletableFuture()
        .thenApply(this::convertResponse);
  }

  private Request convertRequest(HttpRequest request) {
    return new RequestBuilder()
        .setBody(request.getBodyBytes())
        .setMethod(request.getMethod())
        .setQueryParams(convertQueryParams(request.getQueryParams()))
        .setUrl(request.getUrl())
        .setHeaders(convertHeaders(request))
        .build();
  }

  private Map<String, List<String>> convertQueryParams(List<QueryParam> hollerParams) {
    return hollerParams.stream()
        .collect(Collectors.toMap(QueryParam::getKey, QueryParam::getValues));
  }

  private HttpHeaders convertHeaders(HttpRequest request) {
    HttpHeaders httpHeaders = new DefaultHttpHeaders();
    for (Header header : request.getHeaders()) {
      httpHeaders.add(header.getKey(), header.getValue());
    }
    httpHeaders.remove(CONTENT_TYPE);
    httpHeaders.add(CONTENT_TYPE, request.getContentType());
    return httpHeaders;
  }

  private List<Header> convertHeaders(HttpHeaders asyncHeaders) {
    return asyncHeaders.entries().stream()
        .map((e) -> Header.builder().setKey(e.getKey()).addValue(e.getValue()).build())
        .collect(Collectors.toList());
  }

  private HttpResponse convertResponse(Response asyncResponse) {
    return HttpResponse.builder()
        .setBodyAsInputStream(asyncResponse.getResponseBodyAsStream())
        .setStatusCode(asyncResponse.getStatusCode())
        .setHeaders(convertHeaders(asyncResponse.getHeaders()))
        .build();
  }
}
