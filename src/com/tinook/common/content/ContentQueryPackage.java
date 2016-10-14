package com.tinook.common.content;

import java.io.Serializable;

public class ConciseContentQueryPackage implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private Uri target;
	private String[] projection;
	private String selection;
	private String[] selectionArgs;
	private String sortOrder;

	public Uri getTarget() { return target; }
	public ConciseContentQueryPackage setTarget(final Uri target) { this.target = target; return this; }

	public String[] getProjection() { return projection; }
	public ConciseContentQueryPackage setProjection(final String[] projection) { this.projection = projection; return this; }

	public String getSelection() { return selection; }
	public ConciseContentQueryPackage setSelection(final String selection) { this.selection = selection; return this; }

	public String[] getSelectionArgs() { return selectionArgs; }
	public ConciseContentQueryPackage setSelectionArgs(final String[] selectionArgs) { this.selectionArgs = selectionArgs; return this; }

	public String getSortOrder() { return sortOrder; }
	public ConciseContentQueryPackage setSortOrder(final String sortOrder) { this.sortOrder = sortOrder; return this; }
}