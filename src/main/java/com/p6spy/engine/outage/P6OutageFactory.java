
package com.p6spy.engine.outage;

import com.p6spy.engine.event.JdbcEventListener;
import com.p6spy.engine.spy.P6Factory;
import com.p6spy.engine.spy.P6LoadableOptions;
import com.p6spy.engine.spy.option.P6OptionsRepository;

public class P6OutageFactory implements P6Factory {

  @Override
  public JdbcEventListener getJdbcEventListener() {
    return OutageJdbcEventListener.INSTANCE;
  }

  @Override
  public P6LoadableOptions getOptions(P6OptionsRepository optionsRepository) {
    return new P6OutageOptions(optionsRepository);
  }

}
