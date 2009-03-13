package com.p6spy.engine.spy;

public abstract class P6Base {
    protected P6Factory factory;

    public void setP6Factory(P6Factory inVar) {
	factory = inVar;
    }	

    public P6Factory getP6Factory() {
	return factory;
    }
}

