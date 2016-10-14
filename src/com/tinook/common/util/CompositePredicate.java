package com.tinook.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class CompositePredicate<T> extends Predicate<T>
{
	protected List<Predicate<T>> clauses = new ArrayList<Predicate<T>>();

	public CompositePredicate(final Predicate<T> ... clauses) {
		this.clauses.addAll(Arrays.asList(clauses));
	}
}