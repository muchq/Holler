package com.muchq.holler.core;

import com.muchq.immutables.MoonStyle;
import com.muchq.json.JsonUtils;
import io.netty.handler.codec.http.HttpMethod;
import org.immutables.value.Value.Default;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Lazy;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Immutable
@MoonStyle
public interface HttpRequestIF {
  String getUrl();
  List<QueryParam> getQueryParams();
  Optional<Object> getBody();
  Set<Header> getHeaders();

  @Default
  default String getMethod() {
    return HttpMethod.GET.name();
  }

  @Default
  default String getContentType() {
    return "application/json";
  }

  @Lazy
  default byte[] getBodyBytes() {
    if (getBody().isPresent()) {
      return JsonUtils.writeAsBytes(getBody().get());
    }
    return new byte[0];
  }
}
