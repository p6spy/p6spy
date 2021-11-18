package com.p6spy.engine.common;

import com.p6spy.engine.spy.P6SpyDriver;
import org.junit.Test;

import java.sql.Driver;

import static org.junit.Assert.assertEquals;

public class PreparedStatementInformationTest {

    @Test
    public void getSqlWithValues() {
    Driver driver = new P6SpyDriver();
    StringBuilder sql = new StringBuilder();
    sql.append("select \n");
    sql.append("1 AS COL1 -- test? \n");
    sql.append(", 2 AS COL2 /* test? */ \n");
    sql.append(" /* test multiline comment \n");
    sql.append(" ?? \n");
    sql.append(" */ \n");
    sql.append(" , '?' AS QUESTION \n");
    sql.append("WHERE testParam1 = ? \n");
    sql.append("AND testParam2 = ? \n");
    PreparedStatementInformation preparedStatementInformation = new PreparedStatementInformation(ConnectionInformation.fromDriver(driver), sql.toString());
    preparedStatementInformation.setParameterValue(1, "testParam1");
    preparedStatementInformation.setParameterValue(2, "testParam2");

    StringBuilder expectedValue = new StringBuilder();
    expectedValue.append("select \n");
    expectedValue.append("1 AS COL1 -- test? \n");
    expectedValue.append(", 2 AS COL2 /* test? */ \n");
    expectedValue.append(" /* test multiline comment \n");
    expectedValue.append(" ?? \n");
    expectedValue.append(" */ \n");
    expectedValue.append(" , '?' AS QUESTION \n");
    expectedValue.append("WHERE testParam1 = 'testParam1' \n");
    expectedValue.append("AND testParam2 = 'testParam2' \n");

    assertEquals(expectedValue.toString(), preparedStatementInformation.getSqlWithValues());
  }
}
