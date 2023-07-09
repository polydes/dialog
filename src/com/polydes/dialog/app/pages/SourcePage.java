package com.polydes.dialog.app.pages;

import java.awt.*;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;

import com.polydes.datastruct.ui.UIConsts;
import com.polydes.dialog.app.MiniSplitPane;
import com.polydes.dialog.app.editors.text.Highlighter;
import com.polydes.dialog.app.editors.text.TextArea;
import com.polydes.dialog.data.TextSource;

import stencyl.app.api.nodes.HierarchyModelInterface;
import stencyl.app.api.nodes.NodeCreator;
import stencyl.app.api.nodes.NodeIconProvider;
import stencyl.app.api.nodes.NodeViewProvider;
import stencyl.app.comp.darktree.DarkTree;
import stencyl.app.comp.filelist.TreePage;
import stencyl.core.api.pnodes.DefaultBranch;
import stencyl.core.api.pnodes.DefaultLeaf;
import stencyl.core.api.pnodes.HierarchyModel;
import stencyl.core.util.Lang;

public class SourcePage extends JPanel implements NodeCreator<DefaultLeaf, DefaultBranch>
{
	HierarchyModelInterface<DefaultLeaf,DefaultBranch> modelInterface;
	HierarchyModel<DefaultLeaf,DefaultBranch> model;
	TreePage<DefaultLeaf,DefaultBranch> treePage;
	MiniSplitPane splitPane;

	protected Highlighter textHighlighter = TextSource.basicHighlighter;

	private final Map<TextSource, TextArea> editors = new IdentityHashMap<>();
	
	public SourcePage(HierarchyModel<DefaultLeaf,DefaultBranch> model)
	{
		super(new BorderLayout());
		
		this.model = model;
		modelInterface = new HierarchyModelInterface<>(model);
		SourcePageUiProvider uiProvider = new SourcePageUiProvider();
		treePage = new TreePage<>(modelInterface);
		treePage.setNodeIconProvider(uiProvider);
		treePage.setNodeViewProvider(uiProvider);
		modelInterface.setNodeCreator(this);
		
		add(splitPane = new MiniSplitPane(), BorderLayout.CENTER);
		
		splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(treePage.getTree());
		splitPane.setRightComponent(treePage);
		splitPane.setDividerLocation(DarkTree.DEF_WIDTH);
	}

	/*================================================*\
	 | Node UI
	\*================================================*/

	public void setTextHighlighter(Highlighter textHighlighter)
	{
		this.textHighlighter = textHighlighter;
	}

	protected final class SourcePageUiProvider implements
		NodeIconProvider<DefaultLeaf>,
		NodeViewProvider<DefaultLeaf,DefaultBranch>
	{
		@Override
		public ImageIcon getIcon(DefaultLeaf object)
		{
			if(object instanceof DefaultBranch)
			{
				return UIConsts.folderIcon;
			}

			return null;
		}

		@Override
		public JPanel getView(DefaultLeaf object)
		{
			if(object instanceof TextSource textSource)
				return editors.computeIfAbsent(textSource, ts -> new TextArea(ts, textHighlighter));
			return null;
		}

		@Override
		public void disposeView(DefaultLeaf object)
		{
			if(object instanceof TextSource textSource)
			{
				if(editors.remove(textSource) instanceof TextArea textArea)
					textArea.dispose();
			}
		}
	}
	
	public void saveEditors()
	{
		if(model.getRootBranch().isDirty())
		{
			saveEditors(model.getRootBranch());
		}
	}

	private void saveEditors(DefaultBranch branch)
	{
		for(DefaultLeaf leaf : branch.getItems())
		{
			if(leaf.isDirty())
			{
				if(leaf instanceof DefaultBranch subBranch)
					saveEditors(subBranch);
				else if(leaf instanceof TextSource textSource)
					textSource.updateLines(editors.get(textSource).getLines());
			}
		}
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
	public DefaultLeaf createNode(CreatableNodeInfo selected, String nodeName, DefaultBranch newNodeFolder, int insertPosition)
	{
		DefaultLeaf newLeaf;
		if(selected.name.equals("Folder"))
			newLeaf = new DefaultBranch(nodeName);
		else
			newLeaf = new TextSource(nodeName, new ArrayList<>());
		newLeaf.setDirty(true);
		return newLeaf;
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
