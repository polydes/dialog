package com.polydes.dialog.updates;

import java.io.File;

import com.polydes.dialog.DialogExtension;

import stencyl.core.io.FileHelper;
import stencyl.core.lib.Game;
import stencyl.core.util.Worker;

public class V5_GameExtensionUpdate implements Worker
{
	private final Game game;

	public V5_GameExtensionUpdate(Game game)
	{
		this.game = game;
	}

	@Override
	public void doWork()
	{
		DialogExtension dg = DialogExtension.get();
		
		File oldExtrasFolder = game.files.getFile("extras", "[ext] dialog");
		
		FileHelper.copyDirectory(oldExtrasFolder, dg.getExtrasFolder());
		FileHelper.delete(oldExtrasFolder);
	}
}
