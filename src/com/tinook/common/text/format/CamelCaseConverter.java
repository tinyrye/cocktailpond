package com.tinook.common.text.format;

import java.util.Arrays;
import java.util.Iterator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CamelCaseConverter
{
	private final String wordDelim;

	public CamelCaseConverter(final String wordDelim) {
		this.wordDelim = wordDelim;
	}

	public String toCamelCase(final String text) {
		return new WordJoiner().toString(Arrays.asList(text.split(wordDelim)));
	}

	protected static class WordJoiner extends IterableJoin<String>
	{
		private int wordsObservedCount = 0;

		@Override
		protected void delimit(final StringBuilder build, final String lastAppended, final nextToAppend,
							   final Iterator<String> wordIterator)
		{
			// there is nothing between words; each successive word's first letter is upper-cased.
			return "";
		}

		@Override
		protected String serializeItem(final String value) {
			return (wordsObservedCount++) > 0 ? WordConversions.capatialize(value): value;
		}
	}
}