package com.polydes.dialog.app;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.*;

import com.polydes.datastruct.DataStructuresExtension;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.data.structure.StructureDefinitions;
import com.polydes.dialog.app.pages.PluginsPage;

import stencyl.app.api.nodes.HierarchyModelInterface;
import stencyl.app.api.nodes.NodeCreator;
import stencyl.app.api.nodes.NodeCreator.CreatableNodeInfo;
import stencyl.app.api.nodes.NodeCreator.NodeAction;
import stencyl.app.api.nodes.NodeIconProvider;
import stencyl.app.api.nodes.select.NodeSelection;
import stencyl.app.comp.filelist.JListPopupAdapter;
import stencyl.app.comp.filelist.LeafList.LeafRenderer;
import stencyl.app.comp.filelist.LeafListSelectionModel;
import stencyl.app.comp.util.PopupUtil;
import stencyl.core.api.pnodes.DefaultBranch;
import stencyl.core.api.pnodes.DefaultLeaf;
import stencyl.core.api.pnodes.HierarchyRepresentation;
import stencyl.core.api.pnodes.NodeUtils;

import static stencyl.core.util.Lang.asArray;

public class PluginList extends JList<DefaultLeaf> implements HierarchyRepresentation<DefaultLeaf, DefaultBranch>
{
	HierarchyModelInterface<DefaultLeaf,DefaultBranch> modelInterface;
	ArrayList<DefaultLeaf> defs;
	
	DefaultListModel<DefaultLeaf> listModel;
	NodeSelection<DefaultLeaf,DefaultBranch> selection;
	
	public PluginList(HierarchyModelInterface<DefaultLeaf,DefaultBranch> modelInterface, NodeIconProvider<DefaultLeaf> nodeIconProvider)
	{
		super(new DefaultListModel<>());
		listModel = (DefaultListModel<DefaultLeaf>) getModel();
		selection = new NodeSelection<>(modelInterface.getModel());
		
		setBackground(null);
		setCellRenderer(new LeafRenderer<>(60, 48, 24, 24, nodeIconProvider));
		setLayoutOrientation(JList.HORIZONTAL_WRAP);
		setVisibleRowCount(-1);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setSelectionModel(new LeafListSelectionModel<>(modelInterface.getModel(), modelInterface.getModel().getRootBranch(), selection));
		
		setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		
		addMouseListener(new JListPopupAdapter(this) {
			
			@SuppressWarnings("unchecked")
			@Override
			public void showPopup(boolean selectionTargeted, MouseEvent e)
			{
				DefaultLeaf[] targets = selectionTargeted ?
						asArray(getSelectedValuesList(), DefaultLeaf.class) :
						null;
				
				ArrayList<JMenuItem> menuItems = new ArrayList<>();
				
				if(!selectionTargeted)
					menuItems.add(PluginsPage.createNewPlugin.asMenuItem());
				if(selectionTargeted)
				{
					ArrayList<NodeAction<DefaultLeaf>> actionItems = modelInterface.getNodeActions(targets);
					menuItems.addAll(Arrays.asList(PopupUtil.asMenuItems(actionItems)));
				}
				
				JPopupMenu popup = PopupUtil.buildPopup(asArray(menuItems, JMenuItem.class));
				
				PopupUtil.installListener(popup, (item) -> {
					
					if(item instanceof NodeAction)
						for(DefaultLeaf target : targets)
							((NodeAction<DefaultLeaf>) item).callback.accept(target);
					else if(item instanceof NodeCreator.CreatableNodeInfo)
						modelInterface.createNewItem((CreatableNodeInfo) item, selection);
					
				});
				
				Point p = getMousePosition(true);
				if(p == null)
				{
					p = MouseInfo.getPointerInfo().getLocation();
					SwingUtilities.convertPointFromScreen(p, PluginList.this);
				}
				popup.show(PluginList.this, p.x, p.y);
			}
			
		});

		this.modelInterface = modelInterface;
		modelInterface.getModel().addRepresentation(this);
		defs = new ArrayList<>();
		refresh();
	}
	
	@Override
	public int locationToIndex(Point location)
	{
		int index = super.locationToIndex(location);
		if (index != -1 && !getCellBounds(index, index).contains(location))
		{
			return -1;
		}
		else
		{
			return index;
		}
	}
	
	public void dispose()
	{
		modelInterface.getModel().removeRepresentation(this);
	}
	
	public void refresh()
	{
		removeAll();
		defs.clear();
		
		StructureDefinitions sdefs = DataStructuresExtension.get().getStructureDefinitions();
		StructureDefinition dialogExtensionSuperclass = sdefs.getItem("dialog.ds.DialogExtension");
		
		NodeUtils.recursiveRun(modelInterface.getModel().getRootBranch(), (DefaultLeaf leaf) -> {
			if(leaf.getUserData() instanceof StructureDefinition)
			{
				StructureDefinition def = (StructureDefinition) ((DefaultLeaf) leaf).getUserData();
				if(def.parent == dialogExtensionSuperclass)
					defs.add(leaf);
//				NodeUtils.recursiveRun(def.guiRoot, defElement -> {
//					if(((DefaultLeaf) defElement).getObject() instanceof StructureExtension)
//						
//				});
			}
		});
		
		for(DefaultLeaf leaf : defs)
		{
			listModel.addElement(leaf);
		}
	}
	

	/*================================================*\
	 | Folder Hierarchy Representation
	\*================================================*/
	
	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		
	}

	@Override
	public void itemAdded(DefaultBranch folder, DefaultLeaf item, int position)
	{
		refresh();
	}

	@Override
	public void itemRemoved(DefaultBranch folder, DefaultLeaf item, int oldPosition)
	{
		refresh();
	}
}
