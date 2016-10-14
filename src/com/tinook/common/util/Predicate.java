package com.tinook.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class Predicate<X> extends Function<X,Boolean>
{
	/**
	 * Just a rename on {@link get} so that users can call a more precisely
	 * name method if they are using this with external logic.
	 */
	public Boolean matches(final X obj) {
		return get(obj);
	}

	/**
	 * Find all elements in <code>source</code> that pass this predicate.
	 * @return an iterable with all elements in source passing this predicate
	 */
	public Iterable<X> findAll(final Iterable<X> source) {
		final List<X> collection = new ArrayList<X>();
		for (final X sourceElement: source) { if (get(sourceElement)) collection.add(sourceElement); }
		return collection;
	}

	/**
	 * Find all elements in <code>source</code> that pass this predicate.
	 * @return an iterable with all elements in source passing this predicate
	 */
	public <C extends Collection<X>> C findAll(final Iterable<X> source, final C collection) {
		for (final X sourceElement: source) { if (get(sourceElement)) collection.add(sourceElement); }
		return collection;
	}

	/**
	 * Remove all elements in <code>source</code> that pass this predicate.
	 * @return <code>this</code>
	 */
	public Predicate<X> removeAll(final Iterable<X> source) {
		final Iterator<X> sourceItr = source.iterator();
		while (sourceItr.hasNext()) { if (get(sourceItr.next())) sourceItr.remove(); }
		return this;
	}

	/**
	 * Keep/retain all elements in <code>source</code> that pass this predicate.
	 * @return <code>this</code>
	 */
	public Predicate<X> retainAll(final Iterable<X> source) {
		final Iterator<X> sourceItr = source.iterator();
		while (sourceItr.hasNext()) { if (! get(sourceItr.next())) sourceItr.remove(); }
		return this;
	}
}