package com.polydes.dialog.data.def.elements;

import javax.swing.*;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import com.polydes.datastruct.data.structure.SDE;
import com.polydes.datastruct.data.structure.SDEType;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.ui.objeditors.StructureDefinitionEditor;
import com.polydes.datastruct.ui.table.Card;
import com.polydes.datastruct.ui.table.GuiObject;
import com.polydes.datastruct.ui.table.PropertiesSheet;
import com.polydes.datastruct.ui.table.RowGroup;
import com.polydes.dialog.app.editors.text.DialogHighlighter;
import com.polydes.dialog.data.DgTypes;
import com.polydes.dialog.data.def.elements.StructureArgument.Type;

import stencyl.core.api.data.DataList;
import stencyl.core.api.datatypes.DataContext;
import stencyl.core.api.pnodes.DefaultBranch;
import stencyl.core.api.pnodes.DefaultLeaf;
import stencyl.core.io.XML;
import stencyl.core.io.XmlHelper;
import stencyl.core.util.ColorUtil;
import stencyl.core.util.Lang;

public class StructureCommand extends SDE
{
	public String name;
	public DataList/*<StructureArgument>*/ args;
	public String description;

	public StructureCommand(String name, DataList/*<StructureArgument>*/ args, String description)
	{
		this.name = name;
		this.args = args;
		this.description = description;
	}

	@Override
	public String getDisplayLabel()
	{
		return "<" + name + (args.isEmpty() ? ">" :
			" " + StringUtils.join(Lang.mapCA(args, String.class, (arg) -> ((StructureArgument) arg).type.name()), " ") + ">");
	}

	public String getFullHtmlDisplayLabel()
	{
		return String.format("<b><font color=\"%s\">%s<font color=\"%s\">%s</font>%s</font></b> - %s",
			ColorUtil.encode24(DialogHighlighter.TEXT_COLOR_TAG),
			StringEscapeUtils.escapeHtml4("<" + name),
			ColorUtil.encode24(DialogHighlighter.TEXT_COLOR_TAG_DATA),
			args.isEmpty()? "" : " " + StringUtils.join(Lang.mapCA(args, String.class, this::getSArgInfo), " "),
			StringEscapeUtils.escapeHtml4(">"),
			description);
	}

	private String getSArgInfo(Object argObj /* StructureArgument */)
	{
		StructureArgument arg = (StructureArgument) argObj;
		return arg.name+":"+arg.type.name();
	}

	public static class CommandType extends SDEType<StructureCommand>
	{
		public CommandType()
		{
			sdeClass = StructureCommand.class;
			tag = "cmd";
			isBranchNode = false;
			icon = null;
			childTypes = null;
		}

		@Override
		public StructureCommand read(StructureDefinition model, Element e)
		{
			return new StructureCommand(XML.read(e, "name"), readArgs(e), XML.read(e, "desc"));
		}

		private DataList/*<StructureArgument>*/ readArgs(Element e)
		{
			DataList/*<StructureArgument>*/ args = new DataList(DgTypes.sat.getRef());
			XmlHelper.children(e).forEach((child) ->
				args.add(new StructureArgument(XML.read(child, "name"), Type.valueOf(XML.read(child, "type"))))
			);
			return args;
		}

		@Override
		public void write(StructureCommand object, Element e, DataContext ctx)
		{
			e.setAttribute("name", object.name);
			e.setAttribute("desc", object.description);

			for(Object argObj : object.args)
			{
				StructureArgument arg = (StructureArgument) argObj;
				Element child = e.getOwnerDocument().createElement("arg");
				XML.write(child, "name", arg.name);
				XML.write(child, "type", arg.type.name());
				e.appendChild(child);
			}
		}

		@Override
		public StructureCommand create(StructureDefinition def, StructureDefinitionEditor defEditor, String nodeName)
		{
			return new StructureCommand(nodeName, new DataList(DgTypes.sat.getRef()), "");
		}

		@Override
		public GuiObject psAdd(PropertiesSheet sheet, DefaultBranch parent, DefaultLeaf node, StructureCommand value, int i)
		{
			RowGroup extGroup = (RowGroup) sheet.guiMap.get(parent.getParent());
			Card parentCard = extGroup.getSubcard();

			RowGroup group = new RowGroup(value);
			group.add(i == 0 ? sheet.style.createLabel("Commands") : null, sheet.style.createDescriptionRow(value.getFullHtmlDisplayLabel()));
			group.add(sheet.style.hintgap);

			parentCard.addGroup(i + 1, group);

			if(!sheet.isChangingLayout)
				parentCard.layoutContainer();

			return group;
		}

		@Override
		public void psRefresh(PropertiesSheet sheet, GuiObject gui, DefaultLeaf node, StructureCommand value)
		{

		}

		@Override
		public void psRemove(PropertiesSheet sheet, GuiObject gui, DefaultLeaf node, StructureCommand value)
		{
			RowGroup group = (RowGroup) gui;
			Card card = group.card;

			int groupIndex = card.indexOf(group);
			card.removeGroup(groupIndex);

			card.layoutContainer();
		}

		@Override
		public void psLightRefresh(PropertiesSheet sheet, GuiObject gui, DefaultLeaf node, StructureCommand value)
		{
			sheet.style.setDescription((JLabel) ((RowGroup) gui).rows[0].components[1], value.getFullHtmlDisplayLabel());
		}
	}
}
