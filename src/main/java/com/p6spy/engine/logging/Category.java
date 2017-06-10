
package com.p6spy.engine.logging;


/**
 * Filter-like category implementation. Please note these are supposed to be used for logging.
 * 
 * @author Peter Butkovic
 *
 */
public class Category {
	
	public static final Category ERROR = new Category("error");
	public static final Category WARN = new Category("warn");
	public static final Category INFO = new Category("info");
	public static final Category DEBUG = new Category("debug");

	public static final Category BATCH = new Category("batch");
	public static final Category STATEMENT = new Category("statement");
	public static final Category RESULTSET = new Category("resultset");
	public static final Category COMMIT = new Category("commit");
	public static final Category ROLLBACK = new Category("rollback");
	public static final Category RESULT = new Category("result");
	public static final Category OUTAGE = new Category("outage");

	private final String name;

	public Category(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Category other = (Category) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
