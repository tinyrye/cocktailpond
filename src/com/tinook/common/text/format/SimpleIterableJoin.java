package com.tinook.common.text.format;

import java.util.Iterator;

public class SimpleIterableJoin<T> extends IterableJoin<T>
{
	private final String fixedDelimiter;

	public SimpleIterableJoin(final String fixedDelimiter) {
		this.fixedDelimiter = fixedDelimiter;
	}

	/**
	 * @param nextToAppend was the last element pulled from <code>iteration</code> but not yet
	 * appended to <code>build</code>
	 * @param iteration The iteration being joined.  In its current state <code>nextToAppend</code>
	 * was the last element pulled with {@link Iterator#next}.  You would typically use this
	 * to determine if <code>nextToAppend</code> is the last element.
	 */
	protected String delimit(final StringBuilder build, final T lastAppended, final T nextToAppend,
		                     final Iterator<T> iteration) {
		return fixedDelimiter;
	}
}