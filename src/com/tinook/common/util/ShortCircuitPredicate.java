package com.tinook.common.util;

import java.util.Iterator;

import android.util.Log;

import com.tinook.common.text.format.SimpleIterableJoin;

public abstract class ShortCircuitPredicate<T> extends CompositePredicate<T>
{
	public ShortCircuitPredicate(final Predicate<T> ... clauses) {
		super(clauses);
	}

	/**
	 *
	 */
	@Override
	public Boolean get(final T obj)
	{
		Boolean result = Boolean.FALSE;
		final Iterator<Predicate<T>> clauseItr = clauses.iterator();

		while (clauseItr.hasNext()) {
			result = clauseItr.next().get(obj);
			if (shortCircuit(result)) return result;
		}

		return result;
	}

	/**
	 *
	 */
	@Override
	public String describe()
	{
		return String.format(
					"%s: { %s }", super.describe(),
					new SimpleIterableJoin<Predicate<T>>(", ") {
						@Override
						public String serializeItem(final Predicate<T> clause) {
							return String.format("[%s]", clause.describe());
						}
					}.toString(clauses));
	}

	/**
	 * @return <code>true</code> if and only if <code>clauseResult</code>; <code>false</code> otherwise.
	 */
	public abstract Boolean shortCircuit(final Boolean clauseResult);
}