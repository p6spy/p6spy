
package com.p6spy.engine.common;

/**
 * Alternative hash code implementation for the usage with {@link CustomHashedHashSet}.
 * @author Peter Butkovic
 *
 */
public interface Hasher {
	public int getHashCode(Object object);
}
