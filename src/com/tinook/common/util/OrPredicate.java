package com.tinook.common.util;

public class OrPredicate<T> extends ShortCircuitPredicate<T>
{
	public OrPredicate(final Predicate<T> ... clauses) {
		super(clauses);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean shortCircuit(final Boolean clauseResult) {
		return clauseResult;
	}
}