package com.polydes.dialog.app.pages;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.polydes.datastruct.DataStructuresExtension;
import com.polydes.datastruct.data.folder.Folder;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.ui.StructureDefinitionIconProvider;
import com.polydes.datastruct.ui.page.CreateStructureDefinitionDialog;
import com.polydes.datastruct.ui.page.StructureDefinitionPage;
import com.polydes.datastruct.ui.page.StructureDefinitionsWindow;
import com.polydes.dialog.DialogExtension;
import com.polydes.dialog.app.PluginList;
import com.polydes.dialog.app.editors.text.TextArea;
import com.polydes.dialog.data.Snippets;
import com.polydes.dialog.data.def.elements.StructureExtension;

import stencyl.app.api.nodes.DefaultNodeCreator;
import stencyl.app.api.nodes.HierarchyModelInterface;
import stencyl.app.api.nodes.NodeCreator.CreatableNodeInfo;
import stencyl.app.api.nodes.NodeCreator.NodeAction;
import stencyl.app.api.nodes.NodeIconProvider;
import stencyl.app.api.nodes.select.NodeSelection;
import stencyl.app.comp.HorizontalDivider;
import stencyl.app.comp.TitledPanel;
import stencyl.app.comp.UI;
import stencyl.app.comp.dg.MessageDialog;
import stencyl.core.SWC;
import stencyl.core.api.pnodes.DefaultBranch;
import stencyl.core.api.pnodes.DefaultLeaf;
import stencyl.core.api.pnodes.HierarchyModel;
import stencyl.core.api.pnodes.NodeUtils;
import stencyl.core.ext.res.ResourceLoader;
import stencyl.core.ext.res.Resources;
import stencyl.core.lib.IProject;
import stencyl.core.lib.code.snippet.Snippet;
import stencyl.sw.app.doc.Workspace;
import stencyl.sw.core.lib.game.Game;

public class PluginsPage extends JPanel
{
	private static final Logger log = Logger.getLogger(PluginsPage.class);
	
	private static Resources res = ResourceLoader.getResources("com.polydes.dialog");
	
	private static PluginsPage _instance;
	
	private HierarchyModel<DefaultLeaf, DefaultBranch> dialogDefsFM;
	private HierarchyModel<DefaultLeaf, DefaultBranch> userDefsFM;
	private HierarchyModelInterface<DefaultLeaf, DefaultBranch> dialogDefsFmUi;
	private HierarchyModelInterface<DefaultLeaf, DefaultBranch> userDefsFmUi;
	
	private PluginList dialogDefsList;
	private PluginList userDefsList;
	
	public static NodeAction<DefaultLeaf> editPluginStructure = new NodeAction<>("Edit Structure", null, leaf -> {
		SwingUtilities.invokeLater(() -> {
			StructureDefinition selectDef = (StructureDefinition) leaf.getUserData();
			StructureDefinitionPage.get().selectDefinition(selectDef);
		});
		StructureDefinitionsWindow.get().setVisible(true);
	});
	
	public static NodeAction<DefaultLeaf> editPluginCode = new NodeAction<>("Edit Code", null, leaf -> {
		StructureDefinition def = (StructureDefinition) leaf.getUserData();
		IProject project = DataStructuresExtension.get().getProject();

		NodeUtils.recursiveRun(def.guiRoot, (DefaultLeaf defLeaf) -> {
			if (defLeaf.getUserData() instanceof StructureExtension)
			{
				String implementingClass = ((StructureExtension) defLeaf.getUserData()).implementation;
				Snippet toEdit = ((Game) project).getSnippetByClassname(implementingClass);
				if (toEdit == null)
					MessageDialog.showErrorDialog("No implementation", "Couldn't find behavior with classname \"" + implementingClass + "\".");
				SWC.get(Workspace.class).openResource(toEdit, false);
			}
		});
	});
	
	public static NodeAction<DefaultLeaf> duplicatePlugin = new NodeAction<>("Duplicate", null, leaf -> {

	});
	
	public static CreatableNodeInfo createNewPlugin = new CreatableNodeInfo("Create New Plugin", null, null);
	
	private PluginsPage()
	{
		super(new BorderLayout());
		
		Folder root = DataStructuresExtension.get().getStructureDefinitions().root;
		
		DefaultBranch dialogDefsRoot = (DefaultBranch) root.getItemByName(DialogExtension.get().getManifest().name);
		DefaultBranch userDefsRoot = (DefaultBranch) root.getItemByName("My Structures");
		
		dialogDefsFM = new HierarchyModel<>(dialogDefsRoot, DefaultLeaf.class, DefaultBranch.class);
		userDefsFM = new HierarchyModel<>(userDefsRoot, DefaultLeaf.class, DefaultBranch.class);
		
		dialogDefsFmUi = new HierarchyModelInterface<>(dialogDefsFM);
		
		userDefsFmUi = new HierarchyModelInterface<>(userDefsFM)
		{
			@Override
			public DefaultBranch getCreationParentFolder(NodeSelection<DefaultLeaf, DefaultBranch> state)
			{
				return userDefsRoot;
			}
		};

		dialogDefsFmUi.setNodeCreator(new DefaultNodeCreator<>()
		{
			@Override
			public ArrayList<NodeAction<DefaultLeaf>> getNodeActions(DefaultLeaf[] targets)
			{
				ArrayList<NodeAction<DefaultLeaf>> actions = new ArrayList<>();
				if (targets.length == 1)
				{
					if (targets[0].getUserData() instanceof StructureDefinition)
					{
						actions.add(duplicatePlugin);
					}
				}
				return actions;
			}
		});
		
		userDefsFmUi.setNodeCreator(new DefaultNodeCreator<>()
		{
			@Override
			public void nodeRemoved(DefaultLeaf toRemove)
			{
				//TODO
			}

			@Override
			public DefaultLeaf createNode(CreatableNodeInfo selected, String nodeName, DefaultBranch newNodeFolder, int insertPosition)
			{
				IProject project = DialogExtension.get().getProject();
				CreateStructureDefinitionDialog dg = new CreateStructureDefinitionDialog(project);
				dg.setParentClass((StructureDefinition) dialogDefsRoot.getItemByName("Dialog Extension").getUserData());
				dg.setNodeName("New Plugin");
				StructureDefinition toCreate = dg.newDef;
				dg.dispose();

				if (toCreate == null)
					return null;

				String newScriptTemplate = "";
				try
				{
					newScriptTemplate = IOUtils.toString(res.getUrlStream("dialog-extension-template.hx"));
				}
				catch (IOException e)
				{
					log.error(e.getMessage(), e);
				}

				String newScriptName = toCreate.getName();
				String newScriptClass = toCreate.getSimpleClassname();
				String newScriptQualifiedClass = "scripts." + toCreate.getSimpleClassname();

				newScriptTemplate = newScriptTemplate.replaceAll("CLASSNAME", newScriptClass);
				newScriptTemplate = newScriptTemplate.replaceAll("PACKAGE", "scripts");
				newScriptTemplate = newScriptTemplate.replaceAll("NAME", "\"" + newScriptName + "\"");

				Snippets.createNew(project, newScriptName, "scripts", newScriptClass, "Implementation of Dialog plugin.", newScriptTemplate);

				StructureExtension newItem = new StructureExtension(newScriptQualifiedClass, "Description for " + newScriptName);
				DefaultLeaf newLeaf = new DefaultLeaf(newItem.getDisplayLabel(), newItem);
				newLeaf.setDirty(true);
				toCreate.guiRoot.addItem(newLeaf);

				DataStructuresExtension.get().getStructureDefinitions().registerItem(toCreate);
				return toCreate.dref;
			}

			@Override
			public ArrayList<NodeAction<DefaultLeaf>> getNodeActions(DefaultLeaf[] targets)
			{
				ArrayList<NodeAction<DefaultLeaf>> actions = new ArrayList<>();
				if (targets.length == 1)
				{
					if (targets[0].getUserData() instanceof StructureDefinition)
					{
						actions.add(editPluginStructure);
						actions.add(editPluginCode);
						actions.add(duplicatePlugin);
					}
				}
				return actions;
			}

			@Override
			public boolean attemptRemove(List<DefaultLeaf> toRemove)
			{
				//TODO
				return true;
			}
		});
		
		JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		content.setBackground(TextArea.TEXT_EDITOR_COLOR);

		NodeIconProvider<DefaultLeaf> pluginNodeIconProvider = new StructureDefinitionIconProvider();
		
		TitledPanel dialogDefsWrapper = new TitledPanel("Builtin Plugins", null);
		dialogDefsWrapper.add(dialogDefsList = new PluginList(dialogDefsFmUi, pluginNodeIconProvider), BorderLayout.CENTER);
		
		TitledPanel userDefsWrapper = new TitledPanel("Custom Plugins", null);
		userDefsWrapper.add(userDefsList = new PluginList(userDefsFmUi, pluginNodeIconProvider), BorderLayout.CENTER);
		
		content.add(dialogDefsWrapper);
		content.add(new HorizontalDivider(2));
		content.add(userDefsWrapper);
		content.add(Box.createVerticalGlue());
		
		add(UI.createScrollPane(content), BorderLayout.CENTER);
	}

	public static PluginsPage get()
	{
		if (_instance == null)
			_instance = new PluginsPage();
		
		return _instance;
	}
	
	public static void dispose()
	{
		if(_instance != null)
		{
			_instance.dialogDefsList.dispose();
			_instance.userDefsList.dispose();
			_instance.dialogDefsFM.dispose();
			_instance.userDefsFM.dispose();
		}
		_instance = null;
	}
}
