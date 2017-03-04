package com.muchq.holler.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.muchq.holler.core.HttpRequest;
import com.muchq.holler.core.QueryParam;
import io.netty.handler.codec.http.HttpMethod;
import org.asynchttpclient.Param;
import org.asynchttpclient.Request;
import org.junit.Test;

import static com.muchq.holler.util.RequestUtils.CONTENT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;

public class RequestUtilsTest {
  private static final String KEY = "key";
  private static final String VAL1 = "val1";
  private static final String VAL2 = "val2";

  private static final String FOO_COM = "http://foo.com";
  private static final String TEXT_PLAIN = "text/plain";

  @Test
  public void itCanMakeQueryParamWithSingleValue() {
    QueryParam param = RequestUtils.param(KEY, VAL1);
    assertThat(param.getKey()).isEqualTo(KEY);
    assertThat(param.getValues()).isEqualTo(Lists.newArrayList(VAL1));
  }

  @Test
  public void itCanMakeQueryParamWithMultipleValues() {
    QueryParam param = RequestUtils.param(KEY, VAL1, VAL2);
    assertThat(param.getKey()).isEqualTo(KEY);
    assertThat(param.getValues()).isEqualTo(Lists.newArrayList(VAL1, VAL2));
  }

  @Test
  public void itCanConvertHttpRequestToAsyncRequestAndDefaultsToGet() {
    Request asyncRequest = RequestUtils.toAsyncRequest(requestBuilder().build());
    assertUrlAndContentType(asyncRequest);
    assertThat(asyncRequest.getMethod()).isEqualTo(HttpMethod.GET.name());
  }

  @Test
  public void itCanConvertPostRequests() {
    Request asyncRequest = RequestUtils.toAsyncRequest(requestBuilder().setMethod("POST").build());
    assertUrlAndContentType(asyncRequest);
    assertThat(asyncRequest.getMethod()).isEqualTo(HttpMethod.POST.name());
  }

  @Test
  public void itCanConvertRequestsWithQueryParams() {
    HttpRequest request = requestBuilder()
        .addQueryParams(RequestUtils.param(KEY, VAL1, VAL2))
        .build();
    Request asyncRequest = RequestUtils.toAsyncRequest(request);
    assertThat(asyncRequest.getQueryParams()).isEqualTo(Lists.newArrayList(new Param(KEY, VAL1), new Param(KEY, VAL2)));
  }

  @Test
  public void itCanConvertRequestsWithBody() {
    Foo body = new Foo("this is a nice message");
    HttpRequest request = requestBuilder()
        .setBody(body)
        .build();
    Request asyncRequest = RequestUtils.toAsyncRequest(request);
    assertThat(asyncRequest.getByteData()).isEqualTo(request.getBodyBytes());
  }

  private void assertUrlAndContentType(Request request) {
    assertThat(request.getUrl()).isEqualTo(FOO_COM);
    assertThat(request.getHeaders().get(CONTENT_TYPE)).isEqualTo(TEXT_PLAIN);
  }

  private HttpRequest.Builder requestBuilder() {
    return HttpRequest.builder()
        .setUrl(FOO_COM)
        .setContentType(TEXT_PLAIN);
  }

  private static class Foo {
    private final String message;

    @JsonCreator
    public Foo(@JsonProperty("msg") String message) {
      this.message = Preconditions.checkNotNull(message);
    }

    @JsonProperty("msg")
    public String getMessage() {
      return message;
    }

    @Override
    public int hashCode() {
      return message.hashCode();
    }

    @Override
    public boolean equals(Object other) {
      return other instanceof Foo && ((Foo) other).getMessage().equals(message);
    }
  }
}
