package com.polydes.dialog.data.stores;

import java.io.File;

import com.polydes.datastruct.nodes.DefaultViewableBranch;

public abstract class TextStore extends DefaultViewableBranch
{
	protected TextStore(String name)
	{
		super(name);
	}
	
	public abstract void load(File file);
	public abstract void saveChanges(File file);
}
