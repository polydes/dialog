package com.polydes.dialog.updates;

import java.io.File;

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
		File oldExtrasFolder = project.getFile("extras", "[ext] dialog");
		File newExtrasFolder = new File(project.getFiles().getExtensionExtrasDataLocation("com.polydes.dialog"));

		FileHelper.copyDirectory(oldExtrasFolder, newExtrasFolder);
		FileHelper.delete(oldExtrasFolder);
	}
}
