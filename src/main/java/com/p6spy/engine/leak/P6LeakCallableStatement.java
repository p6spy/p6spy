package com.p6spy.engine.leak;

import java.sql.CallableStatement;
import java.sql.SQLException;

import com.p6spy.engine.logging.P6LogCallableStatement;
import com.p6spy.engine.spy.P6Connection;
import com.p6spy.engine.spy.P6Factory;

public class P6LeakCallableStatement extends P6LogCallableStatement {

    public P6LeakCallableStatement(P6Factory factory, CallableStatement statement, P6Connection conn, String query) {
        super(factory, statement, conn, query);
        P6Objects.open(this);
    }

    @Override
    public void close() throws SQLException {
        P6Objects.close(this);
        super.close();
    }

}
