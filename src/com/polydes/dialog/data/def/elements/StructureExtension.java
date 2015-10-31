package com.polydes.dialog.data.def.elements;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.w3c.dom.Element;

import com.polydes.common.io.XML;
import com.polydes.common.util.Lang;
import com.polydes.datastruct.data.folder.DataItem;
import com.polydes.datastruct.data.folder.Folder;
import com.polydes.datastruct.data.structure.SDE;
import com.polydes.datastruct.data.structure.SDEType;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.data.types.DataEditor;
import com.polydes.datastruct.data.types.UpdateListener;
import com.polydes.datastruct.data.types.builtin.basic.StringType;
import com.polydes.datastruct.ui.objeditors.StructureObjectPanel;
import com.polydes.datastruct.ui.table.Card;
import com.polydes.datastruct.ui.table.GuiObject;
import com.polydes.datastruct.ui.table.PropertiesSheet;
import com.polydes.datastruct.ui.table.PropertiesSheetStyle;
import com.polydes.datastruct.ui.table.RowGroup;

public class StructureExtension extends SDE
{
	public String implementation;
	public String description;
	
	public StructureExtension(String implementation, String description)
	{
		this.implementation = implementation;
		this.description = description;
	}
	
	@Override
	public String getDisplayLabel()
	{
		return "Dialog Extension";
	}
	
	public class StructureExtensionPanel extends StructureObjectPanel
	{
		StructureExtension extension;
		
		String oldImplementation;
		String oldDescription;
		
		DataEditor<String> implementationEditor;
		DataEditor<String> descriptionEditor;
		
		public StructureExtensionPanel(final StructureExtension extension, PropertiesSheetStyle style)
		{
			super(style);
			
			this.extension = extension;
			
			oldImplementation = extension.implementation;
			oldDescription = extension.description;
			
			//=== Implementation
	
			implementationEditor = new StringType.SingleLineStringEditor(null, style);
			implementationEditor.setValue(extension.implementation);
		
			implementationEditor.addListener(new UpdateListener()
			{
				@Override
				public void updated()
				{
					extension.implementation = implementationEditor.getValue();
				}
			});
			
			addGenericRow("Implementation", implementationEditor);
			
			//=== Description
			
			descriptionEditor = new StringType.ExpandingStringEditor(null, style);
			descriptionEditor.setValue(extension.description);
		
			descriptionEditor.addListener(new UpdateListener()
			{
				@Override
				public void updated()
				{
					extension.description = descriptionEditor.getValue();
					preview.lightRefreshDataItem(previewKey);
				}
			});
			
			addGenericRow("Description", descriptionEditor);
		}
		
		public void revert()
		{
			extension.implementation = oldImplementation;
			extension.description = oldDescription;
		}
		
		public void dispose()
		{
			clearExpansion(0);
			oldImplementation = null;
			oldDescription = null;
		}
	}

	private StructureExtensionPanel editor = null;
	
	@Override
	public JPanel getEditor()
	{
		if(editor == null)
			editor = new StructureExtensionPanel(this, PropertiesSheetStyle.LIGHT);
		
		return editor;
	}

	@Override
	public void disposeEditor()
	{
		editor.dispose();
		editor = null;
	}

	@Override
	public void revertChanges()
	{
		editor.revert();
	}
	
	public static class ExtensionType extends SDEType<StructureExtension>
	{
		public ExtensionType()
		{
			sdeClass = StructureExtension.class;
			tag = "extension";
			isBranchNode = true;
			icon = null;
			childTypes = Lang.arraylist(StructureCommands.class, StructureDrawkeys.class);
		}

		@Override
		public StructureExtension read(StructureDefinition model, Element e)
		{
			return new StructureExtension(XML.read(e, "implementation"), XML.read(e, "desc"));
		}

		@Override
		public void write(StructureExtension object, Element e)
		{
			e.setAttribute("implementation", object.implementation);
			e.setAttribute("desc", object.description);
		}
		
		@Override
		public StructureExtension create(StructureDefinition def, String nodeName)
		{
			return new StructureExtension("", "");
		}
		
		@Override
		public GuiObject psAdd(PropertiesSheet sheet, Folder parent, DataItem node, StructureExtension value, int i)
		{
			Card parentCard = sheet.getFirstCardParent(parent);
			
			RowGroup group = new RowGroup(value);
			Card card = new Card("", false);
			group.addSubcard(card, parentCard);
			
			parentCard.addGroup(i, group);
			card.setLineBorder("Dialog Extension Info");
			
			RowGroup descGroup = new RowGroup(null);
			descGroup.add(sheet.style.hintgap);
			descGroup.add(sheet.style.createLabel("Description"), sheet.style.createDescriptionRow(value.description));
			descGroup.add(sheet.style.rowgap);
			card.addGroup(0, descGroup);
			
			if(!sheet.isChangingLayout)
				parentCard.layoutContainer();
			
			return group;
		}
		
		@Override
		public void psRefresh(PropertiesSheet sheet, GuiObject gui, DataItem node, StructureExtension value)
		{
			
		}
		
		@Override
		public void psRemove(PropertiesSheet sheet, GuiObject gui, DataItem node, StructureExtension value)
		{
			RowGroup group = (RowGroup) gui;
			Card card = group.card;
			
			int groupIndex = card.indexOf(group);
			card.removeGroup(groupIndex);
			group.removeSubcard();
			
			card.layoutContainer();
		}

		@Override
		public void psLightRefresh(PropertiesSheet sheet, GuiObject gui, DataItem node, StructureExtension value)
		{
			Card subcard = ((RowGroup) gui).getSubcard();
			sheet.style.setDescription((JLabel) subcard.rows[0].rows[1].components[1], value.description);
		}
		
		@Override
		public void genCode(StructureExtension value, StringBuilder builder)
		{
			builder.append("\tpublic var resolveMe:").append(value.implementation).append(";\n");
			builder.append("\tpublic var implementation:String = \"").append(value.implementation).append("\";\n");
		}
	}
}
