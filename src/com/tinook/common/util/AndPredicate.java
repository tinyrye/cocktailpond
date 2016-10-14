package com.tinook.common.util;

public class AndPredicate<T> extends ShortCircuitPredicate<T>
{
	public AndPredicate(final Predicate<T> ... clauses) {
		super(clauses);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean shortCircuit(final Boolean clauseResult) {
		return ! clauseResult;
	}
}