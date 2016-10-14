package com.tinook.cocktailpond.model;

import java.io.Serializable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tinook.common.text.format.ParameterizedText;

public class Volume implements Serializable
{
	private static final long serialVersionUID = 1L;
	public static final Pattern QUANTIFIED_VOLUME_PATTERN = Pattern.compile("\\s*(\\d+(?:\\.\\d+)?)\\s*(\\w*)\\s*");

	public static Volume parse(final String text)
	{
		final Matcher match = QUANTIFIED_VOLUME_PATTERN.matcher(text);

		if (match.find()) {
			final Volume volume = new Volume();
			volume.setNumber(new Float(match.group(1)));
			if ((match.group(2) != null) && ! match.group(2).isEmpty()) volume.setUnit(match.group(2));
			return volume;
		}
		else {
			throw new IllegalArgumentException("Input is not valid.");
		}
	}

	private Float number;
	private String unit;

	public Volume() {

	}
	
	public Volume(final Float number, final String unit) {
		this.number = number;
		this.unit = unit;
	}

	public Float getNumber() { return number; }
	public void setNumber(final Float number) { this.number = number; }

	public String getUnit() { return unit; }
	public void setUnit(final String unit) { this.unit = unit; }


	@Override
	public String toString() {
		return new ParameterizedText("${number}${unit}")
					.param("number", number).param("unit", unit)
					.toString();
	}
}