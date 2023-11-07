package com.polydes.dialog.app.pages;

import com.polydes.dialog.data.TextSource;
import com.polydes.dialog.data.stores.Dialog;

public class DialogPage extends SourcePage
{
	private static DialogPage _instance;
	
	private DialogPage()
	{
		super(Dialog.get().getFolderModel());
		setTextHighlighter(TextSource.dialogHighlighter);
		
		treePage.getTree().setListEditEnabled(true);
		treePage.getFolderModel().setUniqueLeafNames(true);
	}

	public static DialogPage get()
	{
		if (_instance == null)
			_instance = new DialogPage();

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