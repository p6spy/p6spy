package com.p6spy.engine.leak;

import java.sql.Connection;
import java.sql.SQLException;

import com.p6spy.engine.logging.P6LogConnection;
import com.p6spy.engine.spy.P6Factory;

public class P6LeakConnection extends P6LogConnection {

    public P6LeakConnection(P6Factory factory, Connection conn) throws SQLException {
        super(factory, conn);
        P6Objects.open(this);
    }

    @Override
    public void close() throws SQLException {
        P6Objects.close(this);
        super.close();
    }
}
