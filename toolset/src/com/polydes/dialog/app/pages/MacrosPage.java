package com.polydes.dialog.app.pages;

import com.polydes.dialog.data.TextSource;
import com.polydes.dialog.data.stores.Macros;

public class MacrosPage extends SourcePage
{
	private static MacrosPage _instance;
	
	private MacrosPage()
	{
		super(Macros.get().getFolderModel());
		setTextHighlighter(TextSource.dialogHighlighter);
		
		modelInterface.setNodeCreator(null);
		treePage.getTree().setListEditEnabled(false);
	}

	public static MacrosPage get()
	{
		if (_instance == null)
			_instance = new MacrosPage();

		return _instance;
	}

	public static void saveChanges()
	{
		if(_instance != null)
			_instance.saveEditors();
	}

	public static void disposeInstance()
	{
		if(_instance != null)
		{
			_instance.dispose();
			_instance = null;
		}
	}
}