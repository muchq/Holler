package com.muchq.holler;

import com.muchq.holler.core.HttpRequest;
import com.muchq.holler.core.HttpResponse;
import com.muchq.holler.util.RequestUtils;
import org.asynchttpclient.AsyncHttpClient;

import java.util.concurrent.CompletableFuture;

public class AsyncHttp implements HttpClient {
  private final AsyncHttpClient async;

  public AsyncHttp(AsyncHttpClient async) {
    this.async = async;
  }

  @Override
  public CompletableFuture<HttpResponse> execute(HttpRequest request) {
    return async.executeRequest(RequestUtils.toAsyncRequest(request))
        .toCompletableFuture()
        .thenApply(RequestUtils::toHollerResponse);
  }
}
