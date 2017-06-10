
package com.p6spy.engine.common;

/**
 * {@link Hasher} using {@code class.hashCode()} for object hash computing.
 * 
 * @author Peter Butkovic
 */
public class ClassHasher implements Hasher {

	/* (non-Javadoc)
	 * @see com.p6spy.engine.common.Hasher#getHashCode(java.lang.Object)
	 */
	@Override
	public int getHashCode(Object object) {
		return object.getClass().hashCode();
	}

}
