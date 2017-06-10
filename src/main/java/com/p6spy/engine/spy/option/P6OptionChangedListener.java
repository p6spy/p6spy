
package com.p6spy.engine.spy.option;

public interface P6OptionChangedListener {

  public void optionChanged(final String key, final Object oldValue, final Object newValue);
}
