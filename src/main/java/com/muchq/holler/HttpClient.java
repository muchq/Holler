package com.muchq.holler;

import com.muchq.holler.core.HttpRequest;
import com.muchq.holler.core.HttpResponse;

import java.util.concurrent.CompletableFuture;

public interface HttpClient {
  CompletableFuture<HttpResponse> execute(HttpRequest request);
}
