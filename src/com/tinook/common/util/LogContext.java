package com.tinook.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tinook.common.text.format.ParameterizedText;

public class LogContext
{
	// TODO: implement parent inheritence of context and also frame log statements with
	// contextual data if desired.

	private final String logTag;
	private final Map<String,Object> attributes = new HashMap<String,Object>();

	/**
	 * @param logTag The context's log tag is set to this parameter.
	 */
	public LogContext(final String logTag) {
		this.logTag = logTag;
	}

	/**
	 * @param eventActivityContainer The context's log tag is set to this parameter class' name.
	 */
	public LogContext(final Class<?> eventActivityContainer) {
		this(eventActivityContainer.getName());
	}

	public String getLogTag() {
		return logTag;
	}

	public LogContext putAttribute(final String attributeName, final Object attributeValue) {
		attributes.put(attributeName, attributeValue);
		return this;
	}

	public LogContext removeAttribute(final String attributeName) {
		attributes.remove(attributeName);
		return this;
	}

	public void inheritAttributes(final Map<String,Object> attributes)
	{
		for (final String attributeName: this.attributes.keySet())
		{
			if (! attributes.containsKey(attributeName)) {
				attributes.put(attributeName, this.attributes.get(attributeName));
			}
		}
	}

	public LogEvent newEvent(final int priority, final String logStatement) {
		return new LogEvent(priority, new ParameterizedText(logStatement)).attachTo(this);
	}

	public LogEvent newEvent(final int priority, final ParameterizedText logStatement) {
		return new LogEvent(priority, logStatement).attachTo(this);
	}
}