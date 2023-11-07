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
import org.apache.log4j.Logger;

public class Text
{
	private static final Logger log = Logger.getLogger(Text.class);
	
	private static HashMap<File, FileOutputStream> outstreams = new HashMap<>();
	private static HashMap<File, OutputStreamWriter> writers = new HashMap<>();
	
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
			log.error(e.getMessage(), e);
			return new ArrayList<>();
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
			parts = new LinkedHashMap<>();
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
			parts = new ArrayList<>();
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
		
		Stack<TextFolder> folderStack = new Stack<>();
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
		ArrayList<String> toWrite = new ArrayList<>();
		for(TextObject o : folder.parts.values())
			addSection(toWrite, o);
		writeLines(file, toWrite);
	}
	
	public static void addSection(ArrayList<String> lines, TextObject object)
	{
		if(object instanceof TextFolder folder)
		{

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
			FileUtils.writeLines(file, StandardCharsets.UTF_8.name(), lines, "\n");
		}
		catch (IOException e)
		{
			log.error(e.getMessage(), e);
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
			log.error(e.getMessage(), e);
		}
		outstreams.put(file, os);
		writers.put(file, new OutputStreamWriter(os, StandardCharsets.UTF_8));
	}
	
	public static void writeLine(File file, String s)
	{
		try
		{
			writers.get(file).write(s + "\n");
		}
		catch (IOException e)
		{
			log.error(e.getMessage(), e);
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
			log.error(e.getMessage(), e);
		}
		writers.remove(file);
		outstreams.remove(file);
	}
}
