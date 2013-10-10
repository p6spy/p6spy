package com.p6spy.engine.test;

import java.util.Properties;

import com.p6spy.engine.common.P6ModuleManager;

public class P6TestOptions implements P6TestLoadableOptions {

  private String url;
  private String user;
  private String password;
  
  private String url2;
  private String user2;
  private String password2;
  
  @Override
  public void load(Properties properties) {
    setUrl(properties.getProperty("url"));
    setUser(properties.getProperty("user"));
    setPassword(properties.getProperty("password"));
    setUrl2(properties.getProperty("url2"));
    setUser2(properties.getProperty("user2"));
    setPassword2(properties.getProperty("password2"));
  }

  /**
   * Utility method, to make accessing options from app less verbose.
   * 
   * @return active instance of the {@link P6TestLoadableOptions}
   */
  public static P6TestLoadableOptions getActiveInstance() {
    return P6ModuleManager.getInstance().getOptions(P6TestOptions.class);
  }
  
  @Override
  public String getUrl() {
    return url;
  }

  @Override
  public void setUrl(String url) {
    this.url = url;
  }

  @Override
  public String getUser() {
    return user;
  }

  @Override
  public void setUser(String user) {
    this.user = user;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public String getUrl2() {
    return url2;
  }

  @Override
  public void setUrl2(String url2) {
    this.url2 = url2;
  }

  @Override
  public String getUser2() {
    return user2;
  }

  @Override
  public void setUser2(String user2) {
    this.user2 = user2;
  }

  @Override
  public String getPassword2() {
    return password2;
  }

  @Override
  public void setPassword2(String password2) {
    this.password2 = password2;
  }
}
