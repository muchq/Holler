package com.muchq.holler.model;

import com.muchq.immutables.MoonStyle;
import org.immutables.value.Value.Immutable;

@Immutable
@MoonStyle
public interface WidgetIF {
  int getId();
  String getName();
}
