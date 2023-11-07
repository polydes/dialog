package com.polydes.dialog.data.def.elements;

import javax.swing.*;

import org.w3c.dom.Element;

import com.polydes.datastruct.data.folder.Folder;
import com.polydes.datastruct.data.structure.SDE;
import com.polydes.datastruct.data.structure.SDEType;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.ui.objeditors.StructureDefinitionEditor;
import com.polydes.datastruct.ui.table.Card;
import com.polydes.datastruct.ui.table.GuiObject;
import com.polydes.datastruct.ui.table.PropertiesSheet;
import com.polydes.datastruct.ui.table.RowGroup;

import stencyl.core.api.datatypes.DataContext;
import stencyl.core.api.pnodes.DefaultBranch;
import stencyl.core.api.pnodes.DefaultLeaf;
import stencyl.core.io.XML;

public class StructureDrawkey extends SDE
{
	private String name;

	public StructureDrawkey(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public String getDisplayLabel()
	{
		return name;
	}

	public static class DrawkeyType extends SDEType<StructureDrawkey>
	{
		public DrawkeyType()
		{
			sdeClass = StructureDrawkey.class;
			tag = "drawkey";
			isBranchNode = false;
			icon = null;
			childTypes = null;
		}

		@Override
		public StructureDrawkey read(StructureDefinition model, Element e)
		{
			return new StructureDrawkey(XML.read(e, "name"));
		}

		@Override
		public void write(StructureDrawkey object, Element e, DataContext ctx)
		{
			e.setAttribute("name", object.getName());
		}

		@Override
		public StructureDrawkey create(StructureDefinition def, StructureDefinitionEditor defEditor, String nodeName)
		{
			return new StructureDrawkey(nodeName);
		}

		@Override
		public GuiObject psAdd(PropertiesSheet sheet, DefaultBranch parent, DefaultLeaf node, StructureDrawkey value, int i)
		{
			int offset = 1;

			Folder extF = ((Folder) parent.getParent());
			for(DefaultLeaf di : extF.getItems())
				if(di.getUserData() instanceof StructureCommands)
					offset += ((Folder) di).getItems().size();

			RowGroup extGroup = (RowGroup) sheet.guiMap.get(parent.getParent());
			Card parentCard = extGroup.getSubcard();

			RowGroup group = new RowGroup(value);
			group.add(i == 0 ? sheet.style.createLabel("Drawkeys") : null, sheet.style.createDescriptionRow(value.name));
			group.add(sheet.style.hintgap);

			parentCard.addGroup(i + offset, group);

			if(!sheet.isChangingLayout)
				parentCard.layoutContainer();

			return group;
		}

		@Override
		public void psRefresh(PropertiesSheet sheet, GuiObject gui, DefaultLeaf node, StructureDrawkey value)
		{

		}

		@Override
		public void psRemove(PropertiesSheet sheet, GuiObject gui, DefaultLeaf node, StructureDrawkey value)
		{
			RowGroup group = (RowGroup) gui;
			Card card = group.card;

			int groupIndex = card.indexOf(group);
			card.removeGroup(groupIndex);

			card.layoutContainer();
		}

		@Override
		public void psLightRefresh(PropertiesSheet sheet, GuiObject gui, DefaultLeaf node, StructureDrawkey value)
		{
			sheet.style.setDescription((JLabel) ((RowGroup) gui).rows[0].components[1], value.name);
		}
	}
}
