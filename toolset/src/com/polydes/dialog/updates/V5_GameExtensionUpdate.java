package com.polydes.dialog.updates;

import java.io.File;

import com.polydes.dialog.DialogExtension;

import stencyl.core.io.FileHelper;
import stencyl.core.lib.IProject;
import stencyl.core.util.Worker;

public class V5_GameExtensionUpdate implements Worker
{
	private final IProject project;

	public V5_GameExtensionUpdate(IProject project)
	{
		this.project = project;
	}

	@Override
	public void doWork()
	{
		DialogExtension dg = DialogExtension.get();
		
		File oldExtrasFolder = project.getFile("extras", "[ext] dialog");
		
		FileHelper.copyDirectory(oldExtrasFolder, dg.getExtrasFolder());
		FileHelper.delete(oldExtrasFolder);
	}
}
