package com.tinook.common.text.format;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InlineReplace
{
	public static interface MatchCallback {
		String getReplacement(Matcher matchOnPos);
	}

	private final Pattern pattern;
	private final MatchCallback callback;

	public InlineReplace(final Pattern pattern, final MatchCallback callback) {
		this.pattern = pattern;
		this.callback = callback;
	}

	public String replace(final String from)
	{
		final StringBuffer toBuf = new StringBuffer();
		final Matcher match = pattern.matcher(from);
		while (match.find()) match.appendReplacement(toBuf, callback.getReplacement(match));
		match.appendTail(toBuf);
		return toBuf.toString();
	}
}