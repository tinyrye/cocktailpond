package com.tinook.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.tinook.common.text.format.IterableJoin;
import com.tinook.common.text.format.SimpleIterableJoin;

public abstract class Function<X,Y>
{
	public String describe() { return getClass().getName(); }
	
	protected abstract Y get(X in);

	public Iterable<Y> collect(final Iterable<X> source) {
		final List<Y> collection = new ArrayList<Y>();
		for (final X sourceElement: source) collection.add(get(sourceElement));
		return collection;
	}

	public Iterator<Y> collect(final Iterator<X> source)
	{
		return new ReadOnlyIterator<Y>() {
			@Override
			public boolean hasNext() { return source.hasNext(); }
			@Override
			public Y next() { return Function.this.get(source.next()); }
		};
	}

	public Map<Y,List<X>> groupBy(final Iterable<X> source) {
		final Map<Y,List<X>> collection = new HashMap<Y,List<X>>();
		for (final X sourceElement: source) {
			final Y key = get(sourceElement);
			if (! collection.containsKey(key)) collection.put(key, new ArrayList<X>());
			collection.get(key).add(sourceElement);
		}
		return collection;
	}

	public String joinedString(final Iterable<X> source, final String delimiter) {
		return joinedString(source, new SimpleIterableJoin<Y>(delimiter));
	}

	public String joinedString(final Iterable<X> source, final IterableJoin<Y> joiner) {
		return joiner.toString(collect(source));
	}

	public String joinedString(final Iterator<X> source, final String delimiter) {
		return joinedString(source, new SimpleIterableJoin<Y>(delimiter));
	}

	public String joinedString(final Iterator<X> source, final IterableJoin<Y> joiner) {
		return joiner.toString(collect(source));
	}
}