package com.polydes.dialog.app.pages;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import com.polydes.datastruct.nodes.DefaultEditableLeaf;
import com.polydes.datastruct.nodes.DefaultViewableBranch;
import com.polydes.datastruct.nodes.DefaultViewableBranch.DefaultViewableNodeUIProvider;
import com.polydes.dialog.app.MiniSplitPane;
import com.polydes.dialog.data.TextSource;

import stencyl.core.api.pnodes.DefaultBranch;
import stencyl.core.api.pnodes.DefaultLeaf;
import stencyl.core.util.Lang;
import stencyl.toolset.api.nodes.HierarchyModel;
import stencyl.toolset.api.nodes.NodeCreator;
import stencyl.toolset.comp.darktree.DarkTree;
import stencyl.toolset.comp.filelist.TreePage;

public class SourcePage<T extends DefaultEditableLeaf> extends JPanel implements NodeCreator<DefaultLeaf, DefaultBranch>
{
	TreePage<DefaultLeaf,DefaultBranch> treePage;
	MiniSplitPane splitPane;
	
	public SourcePage(HierarchyModel<DefaultLeaf,DefaultBranch> model)
	{
		super(new BorderLayout());
		treePage = new TreePage<>(model, new DefaultViewableNodeUIProvider<>());
		model.setNodeCreator(this);
		
		add(splitPane = new MiniSplitPane(), BorderLayout.CENTER);
		
		splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(treePage.getTree());
		splitPane.setRightComponent(treePage);
		splitPane.setDividerLocation(DarkTree.DEF_WIDTH);
	}
	
	/*================================================*\
	 | Tree Node Creator
	\*================================================*/
	
	private ArrayList<CreatableNodeInfo> creatableNodeList = Lang.arraylist(new CreatableNodeInfo("Dialog Chunk", null, null));
	
	@Override
	public ArrayList<CreatableNodeInfo> getCreatableNodeList(DefaultBranch branchNode)
	{
		return creatableNodeList;
	}

	@Override
	public DefaultLeaf createNode(CreatableNodeInfo selected, String nodeName)
	{
		if(selected.name.equals("Folder"))
			return new DefaultViewableBranch(nodeName);
		
		return new TextSource(nodeName, new ArrayList<>());
	}
	
	@Override
	public ArrayList<NodeAction<DefaultLeaf>> getNodeActions(DefaultLeaf[] targets)
	{
		return null;
	}

	@Override
	public void editNode(DefaultLeaf DefaultLeaf)
	{
		
	}

	@Override
	public void nodeRemoved(DefaultLeaf toRemove)
	{
		
	}

	@Override
	public boolean attemptRemove(List<DefaultLeaf> toRemove)
	{
		return true;
	}
	
	public void dispose()
	{
		treePage.dispose();
		treePage = null;
	}
}
