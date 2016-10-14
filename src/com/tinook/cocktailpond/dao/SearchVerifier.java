package com.tinook.cocktailpond.dao;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

import com.tinook.common.util.FrequencyMeasure;
import com.tinook.common.util.InlineReplace;
import com.tinook.common.util.LogContext;
import com.tinook.common.util.Predicate;
import com.tinook.common.util.OrPredicate;

public class SearchVerifier
{
	private static final LogContext LOG = new LogContext(SearchVerifier.class);

	public Boolean isValidAndCompleteSearch(final String queryString)
    {
        Boolean validAndComplete = Boolean.FALSE;
        if (queryString != null && ! queryString.trim().isEmpty()) {
            validAndComplete = ! INCOMPLETE_QUERY_CHECK.matches(queryString);
        }

        LOG.newEvent(Log.DEBUG, "isValidAndCompleteSearch(queryString=[${queryString}]) = ${validAndComplete}")
                .param("queryString", queryString)
                .param("validAndComplete", validAndComplete)
            .dispatch();
        
        return validAndComplete;
    }

    protected abstract static class QueryCheck extends Predicate<String>
    {
        protected String getAllCappedVersion(final String queryString) {
            return queryString.trim().toUpperCase();
        }
    }

    /**
     * We'd want a lexical analyzer instead of this guy to see if the query
     * has bad syntax.
     */
    protected static final OrPredicate<String> INCOMPLETE_QUERY_CHECK =
        new OrPredicate<String>(new AndOrPlacementCheck(), new QuoteCountCheck());

    /**
     * Cannot begin or end with "AND" or "OR"
     */
    public static class AndOrPlacementCheck extends QueryCheck
    {
        @Override
        public Boolean get(final String queryString)
        {
            final String queryStringAllCaps = getAllCappedVersion(queryString);
            return (queryStringAllCaps.endsWith("AND")
                    || queryStringAllCaps.startsWith("AND")
                    || queryStringAllCaps.endsWith("OR")
                    || queryStringAllCaps.startsWith("OR"));
        }
    }
    /**
     * Must have even number of quotes.
     */
    public static class QuoteCountCheck extends QueryCheck
    {
        protected int countQuotes(final String queryString) {
            return new FrequencyMeasure(queryString).countChars('\"');
        }

        @Override
        public Boolean get(final String queryString) {
            return ((countQuotes(queryString) % 2) != 0);
        }
    }

    public String correctModifiers(final String queryString) {
        return new AndOrToUpperCase().replace(queryString);
    }

    private static class AndOrToUpperCase extends InlineReplace
    {
        public AndOrToUpperCase()
        {
            super(
                Pattern.compile("\\s+([aA][nN][dD]|[oO][rR])\\s+"),
                new InlineReplace.MatchCallback()
                {
                    @Override
                    public String getReplacement(final Matcher matchOnPosition) {
                        final String text = matchOnPosition.group(1);
                        if (text.toUpperCase().equals("AND")) return " AND ";
                        else if (text.toUpperCase().equals("OR")) return " OR ";
                        else return text;
                    }
                });
        }
    }
}