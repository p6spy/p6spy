package com.p6spy.engine.spy;

public abstract class P6Base {
    private P6Factory factory;

    public P6Base(P6Factory factory) {
        this.factory = factory;
    }

    public P6Factory getP6Factory() {
        return factory;
    }
}

