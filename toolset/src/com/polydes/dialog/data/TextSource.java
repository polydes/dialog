package com.polydes.dialog.data;

import java.util.ArrayList;
import java.util.List;

import com.polydes.dialog.app.editors.text.BasicHighlighter;
import com.polydes.dialog.app.editors.text.DialogHighlighter;
import com.polydes.dialog.app.editors.text.Highlighter;

import stencyl.core.api.pnodes.DefaultLeaf;

public class TextSource extends DefaultLeaf
{
	public static final Highlighter basicHighlighter = new BasicHighlighter();
	public static final Highlighter dialogHighlighter = new DialogHighlighter();
	
	private ArrayList<String> lines;
	
	public TextSource(String name, ArrayList<String> lines)
	{
		super(name, null);
		this.lines = lines;
	}
	
	public void trimLeadingTailingNewlines()
	{
		for(int i = 0; i < lines.size(); ++i)
		{
			if(lines.get(i).isEmpty())
				lines.remove(i);
			else
				break;
		}
		for(int i = lines.size() - 1; i >= 0; --i)
		{
			if(lines.get(i).isEmpty())
				lines.remove(i);
			else
				break;
		}
	}
	
	public ArrayList<String> getLines()
	{
		return lines;
	}
	
	public void updateLines(List<String> lines)
	{
		this.lines = new ArrayList<>(lines);
	}

	public void addLine(String line)
	{
		lines.add(line);
	}
}
