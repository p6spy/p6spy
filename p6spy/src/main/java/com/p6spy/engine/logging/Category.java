package com.p6spy.engine.logging;

public enum Category {
  ERROR("error"), INFO("info"), BATCH("batch"), DEBUG("debug"), STATEMENT("statement"), RESULTSET(
      "resultset"), COMMIT("commit"), ROLLBACK("rollback"), RESULT("result");

  private String name;

  Category(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
  
  @Override
  public String toString() {
    return name;
  }
}
