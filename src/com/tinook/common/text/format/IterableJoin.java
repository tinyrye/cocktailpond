package com.tinook.common.text.format;

import java.util.Iterator;

public abstract class IterableJoin<T>
{
	protected abstract String delimit(StringBuilder build, T lastAppended, T nextToAppend, Iterator<T> iteration);
	protected String serializeItem(T value) { return value != null ? value.toString() : ""; }
	
	public String toString(final Iterable<T> values) {
		return toString(values.iterator());
	}

	public String toString(final Iterator<T> values)
	{
		final StringBuilder build = new StringBuilder();
		T currElement = null;
		T lastElement = null;
		if (values.hasNext())
		{
			currElement = values.next();
			build.append(serializeItem(currElement));
			while (values.hasNext()) {
				lastElement = currElement;
				currElement = values.next();
				build.append(delimit(build, lastElement, currElement, values));
				build.append(serializeItem(currElement));
			}
		}
		return build.toString();
	}
}