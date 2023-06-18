package com.polydes.dialog.data.def.elements;

import javax.swing.*;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import stencyl.core.api.datatypes.DataContext;
import stencyl.core.api.datatypes.DataType;
import stencyl.core.api.datatypes.properties.DataTypeProperties;
import stencyl.toolset.api.datatypes.DataEditor;
import stencyl.toolset.comp.datatypes.enumprim.EnumEditor;
import stencyl.toolset.comp.datatypes.string.SingleLineStringEditor;
import stencyl.toolset.comp.propsheet.PropertiesSheetStyle;

public class StructureArgument
{
	public static enum Type
	{
		Int,
		Float,
		Bool,
		String,
		Color,
		Array,
		Dynamic
	}
	
	public String name;
	public Type type;
	
	public StructureArgument(String name, Type type)
	{
		this.name = name;
		this.type = type;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setType(Type type)
	{
		this.type = type;
	}
	
	public Type getType()
	{
		return type;
	}

	public static class StructureArgumentType extends DataType<StructureArgument>
	{
		public StructureArgumentType()
		{
			super(StructureArgument.class, "structure-argument");
		}

		@Override
		public StructureArgument decode(String s, DataContext ctx)
		{
			if(s == null || !s.contains(":"))
				return new StructureArgument("", Type.String);
			
			String name = StringUtils.substringBefore(s, ":");
			Type type = Type.valueOf(StringUtils.substringAfter(s, ":"));
			return new StructureArgument(name, type);
		}

		@Override
		public String encode(StructureArgument t, DataContext ctx)
		{
			return t.name + ":" + t.type;
		}
		
		@Override
		public String toDisplayString(StructureArgument data)
		{
			return encode(data, DataContext.NO_CONTEXT);
		}
		
		@Override
		public StructureArgument copy(StructureArgument t)
		{
			return new StructureArgument(t.name, t.type);
		}
	}
	
	public static class StructureArgumentEditor extends DataEditor<StructureArgument>
	{
		StructureArgument arg;
		
		final DataEditor<String> nameEditor;
		final DataEditor<Type> typeEditor;
		
		final JComponent[] comps;
		
		public StructureArgumentEditor(DataTypeProperties props, PropertiesSheetStyle style)
		{
			nameEditor = new SingleLineStringEditor(new DataTypeProperties(), style);
			nameEditor.addListener(() -> updated());
			
			typeEditor = new EnumEditor<Type>(Type.class);
			typeEditor.addListener(() -> updated());
			
			comps = ArrayUtils.addAll(nameEditor.getComponents(), typeEditor.getComponents());
		}
		
		@Override
		public void set(StructureArgument t)
		{
			nameEditor.setValue(t.name);
			typeEditor.setValue(t.type);
		}
		
		@Override
		public StructureArgument getValue()
		{
			return new StructureArgument(nameEditor.getValue(), typeEditor.getValue());
		}
		
		@Override
		public JComponent[] getComponents()
		{
			return comps;
		}
		
		@Override
		public void dispose()
		{
			super.dispose();
			nameEditor.dispose();
			typeEditor.dispose();
		}
	}
}
