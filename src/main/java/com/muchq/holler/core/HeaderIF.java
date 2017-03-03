package com.muchq.holler.core;

import com.muchq.immutables.MoonStyle;
import org.immutables.value.Value.Immutable;

import java.util.List;

@Immutable
@MoonStyle
public interface HeaderIF {
  String CONTENT_TYPE = "Content-Type";

  String getKey();
  List<String> getValue();
}
