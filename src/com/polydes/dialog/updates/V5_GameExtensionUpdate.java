package com.polydes.dialog.updates;

import java.io.File;

import com.polydes.dialog.DialogExtension;

import stencyl.sw.util.FileHelper;
import stencyl.sw.util.Locations;
import stencyl.sw.util.Worker;

public class V5_GameExtensionUpdate implements Worker
{
	@Override
	public void doWork()
	{
		DialogExtension dg = DialogExtension.get();
		
		File oldExtrasFolder = new File(Locations.getGameLocation(dg.getGame()) + "extras/[ext] dialog");
		
		FileHelper.copyDirectory(oldExtrasFolder, dg.getExtrasFolder());
		FileHelper.delete(oldExtrasFolder);
	}
}
