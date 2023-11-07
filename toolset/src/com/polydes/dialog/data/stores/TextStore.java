package com.polydes.dialog.data.stores;

import java.io.File;

import stencyl.core.api.pnodes.DefaultBranch;

public abstract class TextStore extends DefaultBranch
{
	protected TextStore(String name)
	{
		super(name);
	}
	
	public abstract void load(File file);
	public abstract void saveChanges(File file);
}
