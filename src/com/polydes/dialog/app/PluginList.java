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
import com.polydes.datastruct.nodes.DefaultViewableBranch.DefaultViewableNodeUIProvider;
import com.polydes.dialog.app.pages.PluginsPage;

import stencyl.core.api.pnodes.DefaultBranch;
import stencyl.core.api.pnodes.DefaultLeaf;
import stencyl.core.api.pnodes.NodeUtils;
import stencyl.toolset.api.nodes.HierarchyModel;
import stencyl.toolset.api.nodes.HierarchyRepresentation;
import stencyl.toolset.api.nodes.NodeCreator;
import stencyl.toolset.api.nodes.NodeCreator.CreatableNodeInfo;
import stencyl.toolset.api.nodes.NodeCreator.NodeAction;
import stencyl.toolset.comp.filelist.JListPopupAdapter;
import stencyl.toolset.comp.filelist.LeafList.LeafRenderer;
import stencyl.toolset.comp.util.PopupUtil;

import static stencyl.core.util.Lang.asArray;

public class PluginList extends JList<DefaultLeaf> implements HierarchyRepresentation<DefaultLeaf, DefaultBranch>
{
	HierarchyModel<DefaultLeaf,DefaultBranch> model;
	ArrayList<DefaultLeaf> defs;
	
	DefaultListModel<DefaultLeaf> listModel;
	
	public PluginList(HierarchyModel<DefaultLeaf,DefaultBranch> model)
	{
		super(new DefaultListModel<>());
		listModel = (DefaultListModel<DefaultLeaf>) getModel();
		
		setBackground(null);
		setCellRenderer(new LeafRenderer<DefaultLeaf,DefaultBranch>(60, 48, 24, 24, new DefaultViewableNodeUIProvider<>()));
		setLayoutOrientation(JList.HORIZONTAL_WRAP);
		setVisibleRowCount(-1);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
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
					ArrayList<NodeAction<DefaultLeaf>> actionItems = model.getNodeActions(targets);
					menuItems.addAll(Arrays.asList(PopupUtil.asMenuItems(actionItems)));
				}
				
				JPopupMenu popup = PopupUtil.buildPopup(asArray(menuItems, JMenuItem.class));
				
				PopupUtil.installListener(popup, (item) -> {
					
					if(item instanceof NodeAction)
						for(DefaultLeaf target : targets)
							((NodeAction<DefaultLeaf>) item).callback.accept(target);
					else if(item instanceof NodeCreator.CreatableNodeInfo)
						model.createNewItem((CreatableNodeInfo) item);
					
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

		this.model = model;
		model.addRepresentation(this);
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
		model.removeRepresentation(this);
	}
	
	public void refresh()
	{
		removeAll();
		defs.clear();
		
		StructureDefinitions sdefs = DataStructuresExtension.get().getStructureDefinitions();
		StructureDefinition dialogExtensionSuperclass = sdefs.getItem("dialog.ds.DialogExtension");
		
		NodeUtils.recursiveRun(model.getRootBranch(), (DefaultLeaf leaf) -> {
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
