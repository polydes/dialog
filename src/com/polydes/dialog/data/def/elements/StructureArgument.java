package com.polydes.dialog.data.def.elements;

import org.apache.commons.lang3.StringUtils;

import stencyl.core.api.datatypes.DataContext;
import stencyl.core.api.datatypes.DataType;

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
}
