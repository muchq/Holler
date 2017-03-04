package com.muchq.holler.core;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.CharStreams;
import com.muchq.holler.exceptions.InvalidHeadersException;
import com.muchq.holler.util.RequestUtils;
import com.muchq.immutables.MoonStyle;
import com.muchq.json.JsonUtils;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Lazy;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;


@Immutable
@MoonStyle
public interface HttpResponseIF {
  List<Header> getHeaders();
  InputStream getBodyAsInputStream();
  int getStatusCode();

  @Lazy
  default Optional<String> getContentType() {
    for (Header header : getHeaders()) {
      if (header.getKey().equals(RequestUtils.CONTENT_TYPE)) {
        if (header.getValue().size() != 1) {
          throw new InvalidHeadersException();
        }
        return Optional.of(header.getValue().get(0));
      }
    }
    return Optional.empty();
  }

  @Lazy
  default String getBodyAsString() {
    try (InputStream is = getBodyAsInputStream()) {
      return CharStreams.toString(new InputStreamReader(is, Charsets.UTF_8));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Lazy
  default Optional<String> getBodyAsStringMaybe() {
    return Optional.ofNullable(Strings.emptyToNull(getBodyAsString()));
  }

  @Lazy
  default <T> T getAs(Class<T> clazz) {
    return JsonUtils.readAs(getBodyAsString(), clazz);
  }

  @Lazy
  default <T> T getAs(TypeReference<T> typeReference) {
    return JsonUtils.readAs(getBodyAsString(), typeReference);
  }
}
