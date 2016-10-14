package com.tinook.common.content.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.net.Uri;

import android.provider.BaseColumns;

public class ContractUtils
{
    public static Uri uriFor(final Class modelClass)
    {
        return new Uri.Builder().scheme("content")
                    .authority(getAuthority(modelClass))
                    .path(WordConversions.decapatialize(modelClass.getSimpleName()))
                    .build();
    }

    public static Uri uriFor(final Class<?> entityClass, final Integer entityId) {
        return uriFor(entityClass).buildUpon().appendPath(entityId.toString()).build();
    }

    public static String getAuthority(final Class class) {
        return String.format(
                    "%s.provider",
                    class.getPackage().getName().replaceAll("\\.(model|models|entity|entities)", ""));
    }

    public static Integer getEntityIdFromUri(final Uri entityUri)
    {
        final String lastPathSegment = entityUri.getLastPathSegment();
        try { return Integer.valueOf(lastPathSegment); }
        catch (NumberFormatException ex) { return null; }
    }
}