package com.tinook.common.text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FrequencyMeasure
{
	private final String subject;

	public FrequencyMeasure(final String subject) {
		this.subject = subject;
	}

	public int countChars(final char value) {
		int count = 0;
		for (int i = 0; i < subject.length(); i++) {
			if (subject.charAt(i) == value) count++;
		}
		return count;
	}

	public int countOccurrences(final String regex) {
		int count = 0;
		final Pattern regexPattern = Pattern.compile(regex);
		final Matcher subjectMatchOnRegex = regexPattern.matcher(subject);
		while (subjectMatchOnRegex.find()) count++;
		return count;
	}
}