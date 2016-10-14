package com.tinook.common.util;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.tinook.common.text.format.ParameterizedText;

public class LogEvent
{
	protected final int priority;
	protected final ParameterizedText logStatement;

	protected LogContext context;
	protected Throwable thrown;

	public LogEvent(final int priority, final ParameterizedText logStatement) {
		this.priority = priority;
		this.logStatement = logStatement;
	}

	public LogEvent thrown(final Throwable thrown) {
		this.thrown = thrown;
		return this;
	}
	
	public LogEvent param(final String name, final Object value) {
		logStatement.getParameters().put(name, value);
		return this;
	}

	public LogEvent onActual() {
		return new LogEvent(priority, logStatement.withParams(new HashMap<String,Object>()))
					.attachTo(context);
	}

	public LogEvent attachTo(final LogContext context) {
		this.context = context;
		return this;
	}

	public void dispatch()
	{
		// at dispatch time pull in contextual attributes - do not override those
		// set by invoker of this event as they override static scope values.
		context.inheritAttributes(logStatement.getParameters());
		final String logTag = context.getLogTag();
		final String logStatement = this.logStatement.toString();

		final LogInvoker invoker = getInvoker();
		
		if (thrown != null) invoker.invoke(logTag, logStatement, thrown);
		else invoker.invoke(logTag, logStatement);
	}

	/**
	 *
	 */
	protected LogInvoker getInvoker() {
		return STANDARD_INVOKERS.get(priority);
	}

	protected static interface LogInvoker
	{
		int priority();
		void invoke(String logTag, String text, Throwable thrown);
		void invoke(String logTag, String text);
    }

	protected static class StandardDebugInvoker implements LogInvoker
	{
		@Override
		public int priority() { return Log.DEBUG; }

		@Override
		public void invoke(final String logTag, final String logText, final Throwable thrown) {
			Log.d(logTag, logText, thrown);
		}

		@Override
		public void invoke(final String logTag, final String logText) {
			Log.d(logTag, logText);
		}
	}

	protected static class StandardErrorInvoker implements LogInvoker
	{
		@Override
		public int priority() { return Log.ERROR; }

		@Override
		public void invoke(final String logTag, final String logText, final Throwable thrown) {
			Log.e(logTag, logText, thrown);
		}

		@Override
		public void invoke(final String logTag, final String logText) {
			Log.e(logTag, logText);
		}
	}

	protected static class StandardInfoInvoker implements LogInvoker
	{
		@Override
		public int priority() { return Log.INFO; }

		@Override
		public void invoke(final String logTag, final String logText, final Throwable thrown) {
			Log.i(logTag, logText, thrown);
		}

		@Override
		public void invoke(final String logTag, final String logText) {
			Log.i(logTag, logText);
		}
	}

	protected static class StandardVerboseInvoker implements LogInvoker
	{
		@Override
		public int priority() { return Log.VERBOSE; }

		@Override
		public void invoke(final String logTag, final String logText, final Throwable thrown) {
			Log.v(logTag, logText, thrown);
		}

		@Override
		public void invoke(final String logTag, final String logText) {
			Log.v(logTag, logText);
		}
	}

	protected static class StandardWarnInvoker implements LogInvoker
	{
		@Override
		public int priority() { return Log.WARN; }

		@Override
		public void invoke(final String logTag, final String logText, final Throwable thrown) {
			Log.w(logTag, logText, thrown);
		}

		@Override
		public void invoke(final String logTag, final String logText) {
			Log.w(logTag, logText);
		}
	}

    protected static final Map<Integer,LogInvoker> STANDARD_INVOKERS = new HashMap<Integer,LogInvoker>();

    static
    {
    	STANDARD_INVOKERS.put(Log.DEBUG, new StandardDebugInvoker());
    	STANDARD_INVOKERS.put(Log.ERROR, new StandardErrorInvoker());
    	STANDARD_INVOKERS.put(Log.INFO, new StandardInfoInvoker());
    	STANDARD_INVOKERS.put(Log.VERBOSE, new StandardVerboseInvoker());
    	STANDARD_INVOKERS.put(Log.WARN, new StandardWarnInvoker());
    }
}