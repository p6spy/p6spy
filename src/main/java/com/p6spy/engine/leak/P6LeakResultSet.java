package com.p6spy.engine.leak;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.p6spy.engine.logging.P6LogResultSet;
import com.p6spy.engine.spy.P6Factory;
import com.p6spy.engine.spy.P6Statement;

public class P6LeakResultSet extends P6LogResultSet {

    public P6LeakResultSet(P6Factory factory, ResultSet resultSet, P6Statement statement, String preparedQuery, String query) {
        super(factory, resultSet, statement, preparedQuery, query);
        P6Objects.open(this);
    }

    @Override
    public void close() throws SQLException {
        P6Objects.close(this);
        super.close();
    }
}
