package com.p6spy.engine.proxy;

import net.sf.cglib.core.DefaultNamingPolicy;
import net.sf.cglib.core.NamingPolicy;
import net.sf.cglib.core.Predicate;

/**
 * Naming policy for CGLIB generated classes.
 */
public class ProxyNamingPolicy extends DefaultNamingPolicy {
  public static NamingPolicy INSTANCE = new ProxyNamingPolicy();

  @Override
  public String getClassName(String prefix, String source, Object key, Predicate names) {
    // Prefix the package name with org.p6spy to avoid problems with using signed jars
    // see https://github.com/p6spy/p6spy/issues/200
    return "org.p6spy."+super.getClassName(prefix, source, key, names);
  }
}
