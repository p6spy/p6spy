package com.p6spy.engine.leak;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.p6spy.engine.spy.P6Connection;
import com.p6spy.engine.spy.P6CoreFactory;
import com.p6spy.engine.spy.P6Statement;

public class P6LeakFactory extends P6CoreFactory {

    public P6LeakFactory() {
    }

    @Override
    public Connection getConnection(Connection conn) throws SQLException {
        return new P6LeakConnection(this, conn);
    }

    @Override
    public PreparedStatement getPreparedStatement(PreparedStatement real, P6Connection conn, String p0) throws SQLException {
        return new P6LeakPreparedStatement(this, real, conn, p0);
    }

    @Override
    public Statement getStatement(Statement statement, P6Connection conn) throws SQLException {
        return new P6LeakStatement(this, statement, conn);
    }

    @Override
    public CallableStatement getCallableStatement(CallableStatement real, P6Connection conn, String p0) throws SQLException {
        return new P6LeakCallableStatement(this, real, conn, p0);
    }

    @Override
    public ResultSet getResultSet(ResultSet real, P6Statement statement, String preparedQuery, String query) throws SQLException {
        return new P6LeakResultSet(this, real, statement, preparedQuery, query);
    }

}
