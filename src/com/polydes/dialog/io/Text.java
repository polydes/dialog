package com.polydes.dialog.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Stack;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.CharUtils;

public class Text
{
	private static HashMap<File, FileOutputStream> outstreams = new HashMap<File, FileOutputStream>();
	private static HashMap<File, OutputStreamWriter> writers = new HashMap<File, OutputStreamWriter>();
	
	private static String convertFromPseudoUnicode(String text)
	{
		int index = 0, lastIndex = 0;
		StringBuilder sb = null;
		
		while((index = text.indexOf("~x", lastIndex)) != -1)
		{
			if(sb == null) sb = new StringBuilder(text.length());
			sb.append(text.substring(lastIndex, index));
			try
			{
				int codepoint = Integer.parseInt(text.substring(index + 2, index + 6), 16);
				sb.appendCodePoint(codepoint);
				lastIndex = index + 6;
			}
			catch(NumberFormatException ex)
			{
				sb.append(text.substring(index, index + 2));
				lastIndex += 2;
			}
		}
		if(sb != null)
		{
			if(lastIndex < text.length())
				sb.append(text.substring(lastIndex, text.length()));
			return sb.toString();
		}
		
		return text;
	}
	
	private static String convertToPseudoUnicode(String text)
	{
		StringBuilder sb = new StringBuilder();
		int index = 0;
		while(index < text.length())
		{
			char ch = text.charAt(index);
			if(CharUtils.isAscii(ch))
			{
				sb.append(ch);
				index += 1;
			}
			else
			{
				sb.append("~x").append(hex(text.codePointAt(index), 4));
				index += 1;
				//TODO: doesn't handle surrogate chars
			}
		}
		
		return sb.toString();
	}
	
	public static String hex(int i, int places)
	{
		String s = Integer.toHexString(i);
		while(s.length() < places)
			s = "0" + s;
		return s;
	}
	
	public static List<String> readLines(File file)
	{
		try
		{
			List<String> lines = FileUtils.readLines(file, StandardCharsets.UTF_8);
			for(int i = 0; i < lines.size(); ++i)
			{
				lines.set(i, convertFromPseudoUnicode(lines.get(i)));
			}
			return lines;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return new ArrayList<String>();
		}
	}
	
	public static String sectionMark = ">";
	public static final String folderStartMark = ">>";
	public static final String folderEndMark = "<<";
	
	public static class TextObject
	{
		public String name;
		
		public TextObject(String name)
		{
			this.name = name;
		}
	}
	
	public static class TextFolder extends TextObject
	{
		public LinkedHashMap<String, TextObject> parts;
		
		public TextFolder(String name)
		{
			super(name);
			parts = new LinkedHashMap<String, Text.TextObject>();
		}
		
		public void add(TextObject object)
		{
			parts.put(object.name, object);
		}
	}
	
	public static class TextSection extends TextObject
	{
		public ArrayList<String> parts;
		
		public TextSection(String name)
		{
			super(name);
			parts = new ArrayList<String>();
		}
		
		public void add(String line)
		{
			parts.add(line);
		}
	}
	
	public static TextFolder readSectionedText(File file, String sectionMark)
	{
		Text.sectionMark = sectionMark;
		TextFolder toReturn = new TextFolder("root");
		TextSection section = null;
		
		Stack<TextFolder> folderStack = new Stack<TextFolder>();
		folderStack.push(toReturn);
		
		for(String line : readLines(file))
		{
			if(line.startsWith(folderStartMark))
			{
				folderStack.push(new TextFolder(line.substring(folderStartMark.length())));
			}
			else if(line.startsWith(folderEndMark))
			{
				TextFolder newFolder = folderStack.pop();
				folderStack.peek().add(newFolder);
			}
			else if(line.startsWith(sectionMark))
			{
				section = new TextSection(line.substring(sectionMark.length()));
				folderStack.peek().add(section);
			}
			else if(section != null && line.trim().length() != 0)
				section.add(line);
		}
		
		return toReturn;
	}
	
	public static void writeSectionedText(File file, TextFolder folder, String sectionMark)
	{
		Text.sectionMark = sectionMark;
		ArrayList<String> toWrite = new ArrayList<String>();
		for(TextObject o : folder.parts.values())
			addSection(toWrite, o);
		writeLines(file, toWrite);
	}
	
	public static void addSection(ArrayList<String> lines, TextObject object)
	{
		if(object instanceof TextFolder)
		{
			TextFolder folder = (TextFolder) object;
			
			lines.add(folderStartMark + folder.name);
			for(String key : folder.parts.keySet())
				addSection(lines, folder.parts.get(key));
			lines.add(folderEndMark);
		}
		else
		{
			TextSection section = (TextSection) object;
			
			lines.add(sectionMark + section.name);
			lines.add("");
			lines.addAll(section.parts);
			lines.add("");
		}
	}
	
	public static void writeLines(File file, Collection<String> lines)
	{
		try
		{
			List<String> outLines = new ArrayList<>(lines);
			for(int i = 0; i < lines.size(); ++i)
			{
				outLines.set(i, convertToPseudoUnicode(outLines.get(i)));
			}
			FileUtils.writeLines(file, StandardCharsets.UTF_8.name(), outLines, "\n");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void startWriting(File file)
	{
		FileOutputStream os = null;
		try
		{
			os = new FileOutputStream(file);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		outstreams.put(file, os);
		writers.put(file, new OutputStreamWriter(os, StandardCharsets.UTF_8));
	}
	
	public static void writeLine(File file, String s)
	{
		try
		{
			writers.get(file).write(convertToPseudoUnicode(s) + "\n");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void closeOutput(File file)
	{
		try
		{
			writers.get(file).close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		writers.remove(file);
		outstreams.remove(file);
	}
}
