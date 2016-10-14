package com.tinook.common.text.format;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParameterFormat
{
    public static interface ParameterSetter {
        String serializeParam(String name, Object value);
    }

    public static class BasicParameterSetter implements ParameterSetter
    {
        @Override
        public String serializeParam(final String paramName, final Object paramValue) {
            return paramValue != null ? paramValue.toString() : "";
        }
    }

    private ParameterSetter valueSerializer = new BasicParameterSetter();

    public ParameterSetter getValueSerializer() { return valueSerializer; }
    public ParameterFormat setValueSerializer(final ParameterSetter valueSerializer) { this.valueSerializer = valueSerializer; return this; }

    public String encodeParameter(final String paramName) {
        return String.format("${%s}", paramName);
    }

    public List<String> listParameters(final String parameterizedText)
    {
        final Set<String> params = new HashSet<String>();
        visitParameters(parameterizedText, new ParameterNameExtractorMatchCallback(params));
        return new ArrayList<String>(params);
    }

    /**
     * @param parameterizedText 
     * @return text where all parameter values are injected + serialized into the parameterized text
     */
    public String setParameters(final String parameterizedText,
                                final Map<String,Object> parameterMap)
    {
        final StringBuffer actualTextBuild = new StringBuffer();
        
        final Matcher visitingMatcher =
            visitParameters(
                parameterizedText,
                new ParameterMatchCallback()
                {
                    @Override
                    public void onMatch(final Matcher matcher)
                    {
                        final String paramName = matcher.group(1);

                        if (! parameterMap.containsKey(paramName)) {
                            throw new NoSuchElementException(String.format("Parameter not found to set: %s", paramName));
                        }
                        
                        matcher.appendReplacement(
                            actualTextBuild,
                            valueSerializer.serializeParam(paramName, parameterMap.get(paramName)));
                    }
                });

        visitingMatcher.appendTail(actualTextBuild);

        return actualTextBuild.toString();
    }

    /**
     * This will iterate through each matched paramter in the paramterized text
     * invoking the callback to consume the match.
     * 
     * @return the matcher that iterated through all the parameter matches; you can
     * view the state of the match assuming that all parameters were visited.
     */
    protected Matcher visitParameters(final String parameterizedText, final ParameterMatchCallback callback)
    {
        final Pattern paramPattern = Pattern.compile(encodeAnyParameterRegex());
        final Matcher paramMatching = paramPattern.matcher(parameterizedText);
        
        while (paramMatching.find()) {
            callback.onMatch(paramMatching);
        }

        return paramMatching;
    }
    
    protected String encodeParameterRegex(final String paramName) {
        return new StringBuilder("\\x24\\{")
                    .append(paramName)
                    .append("\\}")
                .toString();
    }

    protected String encodeAnyParameterRegex() {
        return encodeParameterRegex("(\\w+)");
    }

    /**
     * While iterating through pattern matches of parameterized text,
     * this callback will interact with the iterating Matcher object
     */
    protected static interface ParameterMatchCallback
    {
        /**
         * @param matcher this object currently just found a parameter;
         * its state is just after finding it so you can view the 
         * current match and all details of that match that the matcher
         * can provide.
         */
        void onMatch(Matcher matcher);
    }

    protected static class ParameterNameExtractorMatchCallback implements ParameterMatchCallback
    {
        private final Collection<String> nameCollector;

        public ParameterNameExtractorMatchCallback(final Collection<String> nameCollector) {
            this.nameCollector = nameCollector;
        }

        @Override
        public void onMatch(final Matcher matcher) {
            nameCollector.add(matcher.group(1));
        }
    }
}