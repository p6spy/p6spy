package com.p6spy.engine.leak;

import java.sql.SQLException;
import java.sql.Statement;

import com.p6spy.engine.logging.P6LogStatement;
import com.p6spy.engine.spy.P6Connection;
import com.p6spy.engine.spy.P6Factory;

public class P6LeakStatement extends P6LogStatement {

    public P6LeakStatement(P6Factory factory, Statement statement, P6Connection conn) {
        super(factory, statement, conn);
        P6Objects.open(this);
    }

    @Override
    public void close() throws SQLException {
        P6Objects.close(this);
        super.close();
    }
}
