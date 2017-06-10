
package com.p6spy.engine.spy;

import com.p6spy.engine.event.JdbcEventListener;
import com.p6spy.engine.spy.option.P6OptionsRepository;

public class P6SpyFactory implements P6Factory {

  @Override
  public P6LoadableOptions getOptions(P6OptionsRepository optionsRepository) {
    return new P6SpyOptions(optionsRepository);
  }

  @Override
  public JdbcEventListener getJdbcEventListener() {
    return null;
  }
}
