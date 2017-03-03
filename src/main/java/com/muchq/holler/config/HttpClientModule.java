package com.muchq.holler.config;

import com.muchq.guice.ReinstallableGuiceModule;
import com.muchq.json.ObjectMapperModule;

public class HttpClientModule extends ReinstallableGuiceModule {
  @Override
  protected void configure() {
    install(new ObjectMapperModule());
  }
}
