package com.polydes.dialog.updates;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import com.polydes.common.util.Lang;
import com.polydes.datastruct.DataStructuresExtension;
import com.polydes.datastruct.io.Text;

import stencyl.core.engine.sound.ISoundClip;
import stencyl.core.lib.Game;
import stencyl.core.lib.Resource;
import stencyl.sw.util.FileHelper;
import stencyl.sw.util.Util;
import stencyl.sw.util.Worker;

public class V6_ExtensionSubmodules implements Worker
{
	private class Structure extends LinkedHashMap<String,String>
	{
		public Structure(Map<String,String> map)
		{
			super(map);
		}
	}
	
	private int lastStructureID;
	private File root;
	private ArrayList<Structure> modifiedStructures;
	
	@Override
	public void doWork()
	{
		DataStructuresExtension dse = DataStructuresExtension.get();
		
		lastStructureID = -1;
		root = new File(dse.getExtrasFolder(), "data");
		for(File f : FileHelper.listFiles(root))
		{
			if(f.getName().endsWith(".txt"))
				continue;
			lastStructureID = Math.max(lastStructureID, Util.parseInt(Text.readKeyValues(f).get("struct_id"), -1));
		}
		
		modifiedStructures = new ArrayList<>();
		
		Structure log = make("dialog.ds.ext.Logic", "Dialog/Plugins/Logic");
		Structure ms = make("dialog.ds.ext.MessagingScripts", "Dialog/Plugins/Messaging Scripts");
		Structure ss = make("dialog.ds.ext.SoundScripts", "Dialog/Plugins/Sound Scripts");
		
		for(Structure style : loopDef("dialog.ds.Style"))
		{
			String prefix = "Dialog/Plugins/" + style.get("[NAME]") + " ";
			Structure db = make("dialog.ds.ext.DialogBase", prefix + "Dialog Base");
			Structure ts = make("dialog.ds.ext.TypingScripts", prefix + "Typing Scripts");
			Structure eg = make("dialog.ds.ext.ExtraGlyphs", prefix + "Extra Glyphs");
			Structure cs = make("dialog.ds.ext.CharacterScripts", prefix + "Character Scripts");
			Structure sks = make("dialog.ds.ext.SkipScripts", prefix + "Skip Scripts");
			Structure fs = make("dialog.ds.ext.FlowScripts", prefix + "Flow Scripts");
			Structure te = make("dialog.ds.ext.TextEffects", prefix + "Text Effects");
			Structure dop = make("dialog.ds.ext.DialogOptions", prefix + "Dialog Options");
			
			Map<String,String> map = style;
			
			//Dialog Base
			for (String prop : new String[] {
				"msgWindow", "msgBounds", "msgFont",
				"msgTypeSpeed", "msgStartSound",
				"controlAttribute", "lineSpacing",
				"charSpacing", "clearSound", "closeSound",
				"endSound" })
				set(db, prop, map.remove(prop));
			map.remove("fitMsgToWindow"); //unused
			
			//Typing Scripts
			set(ts, "defaultRandomTypeSounds", stringArrayToSoundArray(map.remove("defaultRandomTypeSounds")));
			set(ts, "characterSkipSFX", map.remove("characterSkipSFX"));
			set(ts, "playTypeSoundOnSpaces", map.remove("playTypeSoundOnSpaces"));
			
			//Extra Glyphs
			set(eg, "glyphPadding", map.remove("glyphPadding"));
			
			//Character Scripts
			for (String prop : new String[] { "nameboxWindow",
				"nameboxFont", "faceImagePrefix",
				"faceRelation" })
				set(cs, prop, map.remove(prop));
			set(cs, "faceOrigin", convertRatioPoint(map.remove("faceOrigin")));
			set(cs, "facePos", convertRatioPoint(map.remove("facePos")));
			set(cs, "faceMsgOffset", convertRectangleToInsets(map.remove("faceMsgOffset"), "[%s,-%s,-%s,%s]"));
			
			//Skip Scripts
			for (String prop : new String[] { "fastSpeed",
				"fastButton", "fastSoundInterval", "zoomSpeed",
				"zoomButton", "zoomSoundInterval",
				"instantButton", "instantSound",
				"skippableDefault" })
				set(sks, prop, map.remove(prop));
			set(sks, "fastSound", stringArrayToSoundArray(map.remove("fastSound")));
			set(sks, "zoomSound", stringArrayToSoundArray(map.remove("zoomSound")));
			
			//Flow Scripts
			for (String prop : new String[] { "advanceDialogButton",
				"waitingSound", "waitingSoundInterval",
				"inputSound", "noInputSoundWithTags" })
				set(fs, prop, map.remove(prop));
			set(fs, "animForPointer", convertAnimation(map.remove("animForPointer")));
			set(fs, "pointerPos", convertRatioPoint(map.remove("pointerPos")));
			
			//Text Effects
			for (String prop : new String[] { "v_maxShakeOffsetX",
				"v_maxShakeOffsetY", "v_shakeFrequency",
				"s_magnitude", "s_frequency", "s_pattern",
				"r_diameter", "r_frequency", "r_pattern",
				"g_start", "g_stop", "g_duration" })
				set(te, prop, map.remove(prop));
			
			//Dialog Options
			for(Entry<Object, Object> entry : Lang.hashmap(
				"optWindow", "windowTemplate",
				"optWindowFont", "windowFont",
				"optCursorType", "cursorType",
				"optCursorImage", "cursorImage",
				"optCursorOffset", "cursorOffset",
				"optCursorWindow", "cursorWindow",
				"optChoiceLayout", "choiceLayout",
				"optSelectButton", "selectButton",
				"optScrollWait", "scrollWait",
				"optScrollDuration", "scrollDuration",
				"optAppearSound", "appearSound",
				"optChangeSound", "changeSound",
				"optConfirmSound", "confirmSound",
				"optItemPadding", "itemPadding",
				"optInactiveTime", "inactiveTime").entrySet())
				set(dop, (String) entry.getValue(), map.remove((String) entry.getKey()));
			
			String extensionList =
				StringUtils.join(
					Lang.mapCA
					(
						Lang.arraylist(log,ms,ss,db,ts,eg,cs,sks,fs,te,dop),
						Integer.class,
						(struct) -> Util.parseInt(((Structure) struct).get("struct_id"), -1)
					),
					","
				);
			extensionList = "[" + extensionList + "]:dialog.ds.DialogExtension";
			map.put("extensions", extensionList);
		}
		
		for(Structure scalingImage : loopDef("dialog.ds.ScalingImageTemplate"))
		{
			Map<String,String> map = scalingImage;
			map.put("image", convertExtrasImage(map.get("image")));
			map.put("origin", convertRatioPoint(map.get("origin")));
			map.put("border", convertPointToInsets(map.remove("border")));
			
			System.out.println("Scaling Image");
			System.out.println(map);
		}
		
		for(Structure tween : loopDef("dialog.ds.TweenTemplate"))
		{
			Map<String,String> map = tween;
			map.put("positionStart", convertRatioPoint(map.get("positionStart")));
			map.put("positionStop", convertRatioPoint(map.get("positionStop")));
			
			System.out.println("Tween");
			System.out.println(map);
		}
		
		for(Structure window : loopDef("dialog.ds.WindowTemplate"))
		{
			Map<String,String> map = window;
			map.put("position", convertRatioPoint(map.get("position")));
			map.put("scaleWidthSize", convertRatioInt(map.get("scaleWidthSize")));
			map.put("scaleHeightSize", convertRatioInt(map.get("scaleHeightSize")));
			map.put("insets", convertRectangleToInsets(map.remove("insets"), "[%s,%s,%s,%s]"));
			
			System.out.println("Window");
			System.out.println(map);
		}
		
		for(Structure s : modifiedStructures)
		{
			File f = new File(root, s.remove("[FILE]"));
			s.remove("[NAME]");
			
			Text.writeKeyValues(f, s);
		}
	}
	
	private Collection<Structure> loopDef(String def)
	{
		ArrayList<Structure> toReturn = new ArrayList<>();
		
		for(File f : FileHelper.listFiles(root))
		{
			if(f.getName().endsWith(".txt"))
				continue;
			Map<String, String> map = Text.readKeyValues(f);
			if(map.get("struct_type").equals(def))
			{
				Structure s = new Structure(map);
				s.put("[NAME]", f.getName());
				s.put("[FILE]", f.getAbsolutePath().substring(root.getAbsolutePath().length()));
				toReturn.add(s);
			}
		}
		
		modifiedStructures.addAll(toReturn);
		
		return toReturn;
	}
	
	private void set(Structure s, String field, String value)
	{
		System.out.println(s.get("[NAME]") + ":" + field + "=" + value);
		s.put(field, value);
		
//		StructureField f = s.getTemplate().getField(field);
//		s.setPropertyFromString(f, value);
//		s.setPropertyEnabled(f, !f.isOptional() || s.getProperty(f) != null);
	}
	
	private Structure make(String def, String path)
	{
		File f = new File(root, path);
		String name = FilenameUtils.getBaseName(f.getAbsolutePath());
		f.getParentFile().mkdirs();
		
		int id = ++lastStructureID;
		
		Structure s = new Structure(Lang.hashmap("[NAME]", name, "[FILE]", path, "struct_id", String.valueOf(id), "struct_type", def));
		modifiedStructures.add(s);
		return s;
	}
	
	private String convertAnimation(String s)
	{
		return s == null ? null : "[" + s.replace("-", ",") + "]";
	}
	
	private String convertExtrasImage(String s)
	{
		return s == null ? null : s + ".png";
	}
	
	private String convertRatioInt(String s)
	{
		return s == null ? null : "[" + s + "]";
	}
	
	private String convertRatioPoint(String s)
	{
		return s == null ? null : s.replaceAll("([^,]+)", "\\[$0\\]");
	}
	
	private String convertRectangleToInsets(String s, String format)
	{
		String[] parts = s.replaceAll("\\[|\\]| ", "").split(",");
		String newValue = String.format(format, parts[1], parts[2], parts[3], parts[0]);
		newValue.replaceAll("--", "").replaceAll("-0","0");
		return newValue;
	}
	
	private String convertPointToInsets(String s)
	{
		if(s == null)
			return "[0,0,0,0]";
		String[] parts = s.replaceAll("\\[|\\]| ", "").split(",");
		String newValue = String.format("[%s,%s,%s,%s", parts[1], parts[0], parts[1], parts[0]);
		return newValue;
	}
	
	private String simplifyArray(String s)
	{
		return s == null ? null : s.replaceAll(":[^,\\]]+", "").replaceAll("\\[|\\]", "");
	}
	
	private String stringArrayToSoundArray(String s)
	{
		if(s == null)
			return null;
		s = simplifyArray(s);
		String[] ids = s.split(",");
		for(int i = 0; i < ids.length; ++i)
		{
			Resource r = Game.getGame().getResources().getResourceWithName(s);
			if(r != null && ISoundClip.class.isAssignableFrom(r.getClass()))
				ids[i] = String.valueOf(r.getID());
			else
				ids[i] = "";
		}
		return "[" + StringUtils.join(ids, ",") + "]:com.stencyl.models.Sound";
	}
}
