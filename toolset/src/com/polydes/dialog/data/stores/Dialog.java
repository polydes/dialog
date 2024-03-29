package com.polydes.dialog.data.stores;

import java.io.File;

import com.polydes.dialog.data.TextSource;
import com.polydes.dialog.io.Text;
import com.polydes.dialog.io.Text.TextFolder;
import com.polydes.dialog.io.Text.TextObject;
import com.polydes.dialog.io.Text.TextSection;

import stencyl.core.api.pnodes.DefaultBranch;
import stencyl.core.api.pnodes.DefaultLeaf;
import stencyl.core.api.pnodes.HierarchyModel;

public class Dialog extends TextStore
{
	private static Dialog _instance;
	
	private HierarchyModel<DefaultLeaf, DefaultBranch> folderModel;
	
	private Dialog()
	{
		super("Dialog");
		folderModel = new HierarchyModel<>(this, DefaultLeaf.class, DefaultBranch.class);
	}
	
	public static Dialog get()
	{
		if(_instance == null)
			_instance = new Dialog();
		
		return _instance;
	}
	
	public HierarchyModel<DefaultLeaf, DefaultBranch> getFolderModel()
	{
		return folderModel;
	}
	
	@Override
	public void load(File file)
	{
		TextFolder root = Text.readSectionedText(file, "#");
		for(TextObject object : root.parts.values())
			load(this, object);
	}
	
	public void load(DefaultBranch f, TextObject o)
	{
		f.markAsLoading(true);
		if(o instanceof TextFolder)
		{
			DefaultBranch newFolder = new DefaultBranch(o.name);
			for(TextObject object : ((TextFolder) o).parts.values())
				load(newFolder, object);
			f.addItem(newFolder);
		}
		else if(o instanceof TextSection)
		{
			f.addItem(new TextSource(o.name, ((TextSection) o).parts));
		}
		f.markAsLoading(false);
	}
	
	@Override
	public void saveChanges(File file)
	{
		if(isDirty())
		{
			TextFolder toWrite = new TextFolder("root");
			for(DefaultLeaf item : getItems())
				save(item, toWrite);
			Text.writeSectionedText(file, toWrite, "#");
		}
		setDirty(false);
	}
	
	public void save(DefaultLeaf item, TextFolder f)
	{
		if(item instanceof DefaultBranch)
		{
			TextFolder newFolder = new TextFolder(item.getName());
			for(DefaultLeaf d : ((DefaultBranch) item).getItems())
				save(d, newFolder);
			f.add(newFolder);
		}
		else
		{
			TextSource source = (TextSource) item;
			
			TextSection newSection = new TextSection(item.getName());
			newSection.parts = source.getLines();
			f.add(newSection);
		}
	}
	
	public void dispose()
	{
		unload();
		folderModel.dispose();
		folderModel = null;
		_instance = null;
	}
}
