package com.tinook.common.util;

import java.util.Iterator;

public abstract class ReadOnlyIterator<T> implements Iterator<T>
{
	@Override
	public void remove() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("The backing iterable is read-only.");
	}
}