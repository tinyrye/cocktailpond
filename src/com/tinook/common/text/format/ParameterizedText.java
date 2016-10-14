package com.tinook.common.text.format;

import java.util.HashMap;
import java.util.Map;

public class ParameterizedText
{
    private final String parameterizedText;
    private Map<String,Object> parameters = new HashMap<String,Object>();

    protected ParameterFormat format = new ParameterFormat();

    public ParameterizedText(final String parameterizedText) {
    	this.parameterizedText = parameterizedText;
    }

    public ParameterizedText(final String parameterizedText, final Map<String,Object> parameters) {
    	this.parameterizedText = parameterizedText;
    	this.parameters = parameters;
    }

    public ParameterFormat getFormat() { return format; }
    public ParameterizedText setFormat(final ParameterFormat format) { this.format = format; return this; }
    
    public Map<String,Object> getParameters() { return parameters; }
    public ParameterizedText setParameters(final Map<String,Object> parameters) { this.parameters = parameters; return this; }
    public ParameterizedText param(final String name, final Object value) { parameters.put(name, value); return this; }
    public ParameterizedText receiveParameters(final Map<String,Object> parameters) {
        if (parameters != null) {
            this.parameters.putAll(parameters);
        }
        return this;
    }

    /**
     * Make an instance with the same parameterized text and format but with separate parameters.
     */
    public ParameterizedText withParams(final Map<String,Object> parameters) {
    	return new ParameterizedText(parameterizedText, parameters).setFormat(format);
    }

    /**
     * Make an instance with the same parameterized text, format, and a copy of the parameters.
     */
    public ParameterizedText withCopyOfParams() {
        return new ParameterizedText(parameterizedText, new HashMap<String,Object>(parameters)).setFormat(format);
    }
    
    @Override
    public String toString() {
    	return format.setParameters(parameterizedText, parameters);
    }
}