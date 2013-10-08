package com.p6spy.engine.test;

import com.p6spy.engine.common.P6LoadableOptions;

public interface P6TestLoadableOptions extends P6LoadableOptions, P6TestOptionsMBean {

  // no need to expose these via MBean => keep them here
  String getUrl();

  void setUrl(String url);

  String getUser();

  void setUser(String user);

  String getPassword();

  void setPassword(String password);

  String getUrl2();

  void setUrl2(String url2);

  String getUser2();

  void setUser2(String user2);

  String getPassword2();

  void setPassword2(String password2);

}
