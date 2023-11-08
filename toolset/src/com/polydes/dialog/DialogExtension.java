package com.polydes.dialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.*;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.polydes.datastruct.DataStructuresExtension;
import com.polydes.datastruct.data.structure.SDEType;
import com.polydes.datastruct.data.structure.SDETypes;
import com.polydes.datastruct.data.structure.elements.StructureTab;
import com.polydes.datastruct.data.types.HaxeDataType;
import com.polydes.datastruct.ext.HaxeDataTypeExtension;
import com.polydes.dialog.app.MainEditor;
import com.polydes.dialog.app.pages.DialogPage;
import com.polydes.dialog.app.pages.MacrosPage;
import com.polydes.dialog.data.DgTypes;
import com.polydes.dialog.data.def.elements.StructureCommand.CommandType;
import com.polydes.dialog.data.def.elements.StructureCommands.CommandsType;
import com.polydes.dialog.data.def.elements.StructureDrawkey.DrawkeyType;
import com.polydes.dialog.data.def.elements.StructureDrawkeys.DrawkeysType;
import com.polydes.dialog.data.def.elements.StructureExtension;
import com.polydes.dialog.data.def.elements.StructureExtension.ExtensionType;
import com.polydes.dialog.data.stores.Dialog;
import com.polydes.dialog.data.stores.Macros;
import com.polydes.dialog.updates.DS_V4_FullTypeNamesUpdate;
import com.polydes.dialog.updates.V5_GameExtensionUpdate;
import com.polydes.dialog.updates.V6_ExtensionSubmodules;

import stencyl.app.ext.PageAddon;
import stencyl.app.ext.PageAddon.EngineExtensionPageAddon;
import stencyl.core.api.datatypes.DataContext;
import stencyl.core.api.fs.Locations;
import stencyl.core.datatypes.Types;
import stencyl.core.ext.ExtensionInterface;
import stencyl.core.ext.GameExtension;
import stencyl.core.ext.engine.ExtensionInstanceManager.FormatUpdateSubmitter;
import stencyl.core.ext.res.ResourceLoader;
import stencyl.core.ext.res.Resources;
import stencyl.core.io.FileHelper;
import stencyl.core.util.Lang;
import stencyl.sw.app.center.GameLibrary;

public class DialogExtension extends GameExtension
{
	private static final Logger log = Logger.getLogger(DialogExtension.class);
	
	private static Resources res = ResourceLoader.getResources("com.polydes.dialog");
	private static DialogExtension _instance;
	
	ArrayList<HaxeDataType> types;
	ArrayList<SDEType<?>> sdeTypes;
	
	public static DialogExtension get()
	{
		return _instance;
	}
	
	@Override
	public void onLoad()
	{
		LogManager.getLogger("com.polydes.dialog").setLevel(Level.DEBUG);
		
		_instance = this;

		PageAddon dialogSidebarPage = new EngineExtensionPageAddon(owner())
		{
			@Override
			public JPanel getPage()
			{
				return MainEditor.get();
			}
		};

		owner().getAddons().setAddon(GameLibrary.DASHBOARD_SIDEBAR_PAGE_ADDONS, dialogSidebarPage);
		
		sdeTypes = Lang.arraylist(
			new ExtensionType(),
			new CommandsType(),
			new CommandType(),
			new DrawkeysType(),
			new DrawkeyType()
		);

		Dialog.get().load(new File(getExtrasFolder(), "dialog.txt"));
		Macros.get().load(new File(getExtrasFolder(), "macros.txt"));

		ExtensionInterface.doUponLoad(getProject(), "com.polydes.datastruct", () -> {

			DataStructuresExtension dse = DataStructuresExtension.get();

			for(SDEType<?> sdet : sdeTypes)
				dse.getSdeTypes().registerItem(getManifest().id, sdet);
			SDETypes.fromClass(StructureTab.class).childTypes.add(StructureExtension.class);
			DgTypes.registerTypes();

			DataContext ctx = DataContext.fromMap(Map.of("Project", getProject()));

			types = HaxeDataTypeExtension.readTypesFolder(new File(Locations.getGameExtensionLocation("com.polydes.dialog"), "types"), ctx);
			for(HaxeDataType type : types)
				dse.getHaxeTypes().registerItem(type);

			File defLoc = new File(Locations.getGameExtensionLocation("com.polydes.dialog"), "def");
			dse.getStructureDefinitions().addFolder(defLoc, getManifest().name);
		});
	}
	
	@Override
	public void onInstalled()
	{
		loadDefaults();
	}

	protected int detectOldInstall()
	{
		return getProject().getFile("extras", "[ext] dialog").exists() ? 4 : -1;
	}

	@Override
	protected void onUninstalled()
	{
		FileHelper.delete(getExtrasFolder());
		FileHelper.delete(getDataFolder());
	}
	
	@Override
	public void updateFromVersion(int fromVersion, FormatUpdateSubmitter updateQueue)
	{
		if(fromVersion < 5)
			updateQueue.add(new V5_GameExtensionUpdate(getProject()));
		if(fromVersion < 6)
		{
			updateQueue
				.before(com.polydes.datastruct.updates.V4_FullTypeNamesUpdate.class)
				.add(new DS_V4_FullTypeNamesUpdate());
			updateQueue
				.after(V5_GameExtensionUpdate.class)
				.after(com.polydes.datastruct.updates.V4_FullTypeNamesUpdate.class)
				.add(new V6_ExtensionSubmodules(getProject()));
		}
	}
	
	private void loadDefaults()
	{
		File f;
		try
		{
			f = new File(getExtrasFolder(), "images" + File.separator + "Default Window.png");
			f.getParentFile().mkdirs();
			if(!f.exists())
				FileHelper.writeToPNG(f.getAbsolutePath(), res.loadImage("defaults/Default Window.png"));
			if(!new File(getExtrasFolder(), "dialog.txt").exists())
				FileUtils.writeStringToFile(new File(getExtrasFolder(), "dialog.txt"), res.loadText("defaults/dialog.txt"));
			if(!new File(getExtrasFolder(), "macros.txt").exists())
				FileUtils.writeStringToFile(new File(getExtrasFolder(), "macros.txt"), res.loadText("defaults/macros.txt"));
		}
		catch (IOException e)
		{
			log.error(e.getMessage(), e);
		}
	}
	
	@Override
	protected void onSave()
	{
		DialogPage.saveChanges();
		MacrosPage.saveChanges();
		
		Dialog.get().saveChanges(new File(getExtrasFolder(), "dialog.txt"));
		Macros.get().saveChanges(new File(getExtrasFolder(), "macros.txt"));
		
		MainEditor.get().gameSaved();
	}

	@Override
	protected void onUnload()
	{
		for(HaxeDataType type : types)
			Types.get().unloadReference(type.dataType);
		DgTypes.unregisterTypes();
		
		SDETypes.fromClass(StructureTab.class).childTypes.remove(StructureExtension.class);
		
		MainEditor.disposePages();
		
		Dialog.get().dispose();
		Macros.get().dispose();
		
		types = null;
	}
}
