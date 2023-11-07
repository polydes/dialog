package com.polydes.dialog.data;

import java.io.IOException;

import stencyl.core.io.FileHelper;
import stencyl.core.lib.IProject;
import stencyl.core.lib.code.snippet.Snippet;
import stencyl.sw.core.lib.game.Game;
import stencyl.sw.core.lib.snippet.SWSnippetType;

public class Snippets
{
	public static Snippet createNew(IProject project, String name, String packageName, String className, String description, String sourceCode)
	{
		try
		{
			int ID = project.getNextResourceID(Snippet.class);

			Snippet s = new Snippet
			(
				(Game) project,
				ID, 
				name, 
				className, 
				-1, 
				description, 
				false,
				-1,
				0,
				SWSnippetType.ARBITRARY,
				false,
				null,
				-1,
				false,
				true,
				packageName
			);

			project.addResource(s);

			project.getFile("code").mkdirs();

			FileHelper.writeStringToFile(project.getFile("code", className + ".hx"), sourceCode);

			return s;
		}

		catch(IOException ex)
		{
			ex.printStackTrace();
			return null;
		}
	}
}
