package com.tinook.common.text.format;

public class WordConversions
{
	public static String capatalize(final String word) {
		final StringBuilder build = new StringBuilder();
		build.append(Character.toUpperCase(word.charAt(0)));
		build.append(word.substring(1));
		return build;
	}

	public static String decapatalize(final String word) {
		final StringBuilder build = new StringBuilder();
		build.append(Character.toLowerCase(word.charAt(0)));
		build.append(word.substring(1));
		return build;
	}
}