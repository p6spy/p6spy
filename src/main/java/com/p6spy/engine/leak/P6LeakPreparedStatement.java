package com.p6spy.engine.leak;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.p6spy.engine.logging.P6LogPreparedStatement;
import com.p6spy.engine.spy.P6Connection;
import com.p6spy.engine.spy.P6Factory;

public class P6LeakPreparedStatement extends P6LogPreparedStatement {

    public P6LeakPreparedStatement(P6Factory factory, PreparedStatement statement, P6Connection conn, String query) {
        super(factory, statement, conn, query);
        P6Objects.open(this);
    }

    @Override
    public void close() throws SQLException {
        P6Objects.close(this);
        super.close();
    }
}
