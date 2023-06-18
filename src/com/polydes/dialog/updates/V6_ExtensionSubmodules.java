package com.polydes.dialog.updates;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.polydes.datastruct.DataStructuresExtension;
import com.polydes.datastruct.io.Text;

import stencyl.core.engine.sound.ISoundClip;
import stencyl.core.io.FileHelper;
import stencyl.core.lib.Game;
import stencyl.core.lib.resource.Resource;
import stencyl.core.util.Lang;
import stencyl.core.util.ParsingHelper;
import stencyl.core.util.Worker;

public class V6_ExtensionSubmodules implements Worker
{
	private static final Logger log = Logger.getLogger(V6_ExtensionSubmodules.class);
	
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
			lastStructureID = Math.max(lastStructureID, ParsingHelper.parseInt(Text.readKeyValues(f).get("struct_id"), -1));
		}
		
		modifiedStructures = new ArrayList<>();
		
		Function<String, String> convertAnim = s -> convertAnimation(s);
		Function<String, String> convertImage = s -> convertExtrasImage(s);
		Function<String, String> convertPointToInsets = s -> convertPointToInsets(s);
		Function<String, String> convertRatioInt = s -> convertRatioInt(s);
		Function<String, String> convertRatioPoint = s -> convertRatioPoint(s);
		Function<String, String> convertStrArrToSndArr = s -> stringArrayToSoundArray(s);
		
		Structure logic = make("dialog.ds.ext.Logic", "Dialog/Plugins/Logic");
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
			
			//Dialog Base
			for (String prop : new String[] {
				"msgWindow", "msgBounds", "msgFont",
				"msgTypeSpeed", "msgStartSound",
				"controlAttribute", "lineSpacing",
				"charSpacing", "clearSound", "closeSound",
				"endSound" })
				move(style, db, prop);
			style.remove("fitMsgToWindow"); //unused
			
			//Typing Scripts
			moveConvert(style, ts, "defaultRandomTypeSounds", convertStrArrToSndArr);
			move(style, ts, "characterSkipSFX");
			move(style, ts, "playTypeSoundOnSpaces");
			
			//Extra Glyphs
			move(style, eg, "glyphPadding");
			
			//Character Scripts
			for (String prop : new String[] { "nameboxWindow",
				"nameboxFont", "faceImagePrefix",
				"faceRelation" })
				move(style, cs, prop);
			moveConvert(style, cs, "faceOrigin", convertRatioPoint);
			moveConvert(style, cs, "facePos", convertRatioPoint);
			moveConvert(style, cs, "faceMsgOffset", s -> convertRectangleToInsets(s, "[%s,-%s,-%s,%s]"));
			
			//Skip Scripts
			for (String prop : new String[] { "fastSpeed",
				"fastButton", "fastSoundInterval", "zoomSpeed",
				"zoomButton", "zoomSoundInterval",
				"instantButton", "instantSound",
				"skippableDefault" })
				move(style, sks, prop);
			moveConvert(style, sks, "fastSound", convertStrArrToSndArr);
			moveConvert(style, sks, "zoomSound", convertStrArrToSndArr);
			
			//Flow Scripts
			for (String prop : new String[] { "advanceDialogButton",
				"waitingSound", "waitingSoundInterval",
				"inputSound", "noInputSoundWithTags" })
				move(style, fs, prop);
			moveConvert(style, fs, "animForPointer", convertAnim);
			moveConvert(style, fs, "pointerPos", convertRatioPoint);
			
			//Text Effects
			for (String prop : new String[] { "v_maxShakeOffsetX",
				"v_maxShakeOffsetY", "v_shakeFrequency",
				"s_magnitude", "s_frequency", "s_pattern",
				"r_diameter", "r_frequency", "r_pattern",
				"g_start", "g_stop", "g_duration" })
				move(style, te, prop);
			
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
				set(dop, (String) entry.getValue(), style.remove((String) entry.getKey()));
			
			String extensionList =
				StringUtils.join(
					Lang.mapCA
					(
						Lang.arraylist(logic,ms,ss,db,ts,eg,cs,sks,fs,te,dop),
						Integer.class,
						(struct) -> ParsingHelper.parseInt(((Structure) struct).get("struct_id"), -1)
					),
					","
				);
			extensionList = "[" + extensionList + "]:dialog.ds.DialogExtension";
			style.put("extensions", extensionList);
		}
		
		for(Structure scalingImage : loopDef("dialog.ds.ScalingImageTemplate"))
		{
			convert(scalingImage, "image", convertImage);
			convert(scalingImage, "origin", convertRatioPoint);
			convert(scalingImage, "border", convertPointToInsets);
			
			log.debug("Scaling Image");
			log.debug(scalingImage);
		}
		
		for(Structure tween : loopDef("dialog.ds.TweenTemplate"))
		{
			convert(tween, "positionStart", convertRatioPoint);
			convert(tween, "positionStop", convertRatioPoint);
			
			log.debug("Tween");
			log.debug(tween);
		}
		
		for(Structure window : loopDef("dialog.ds.WindowTemplate"))
		{
			convert(window, "position", convertRatioPoint);
			convert(window, "scaleWidthSize", convertRatioInt);
			convert(window, "scaleHeightSize", convertRatioInt);
			convert(window, "insets", s -> convertRectangleToInsets(s, "[%s,%s,%s,%s]"));
			
			log.debug("Window");
			log.debug(window);
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
		log.debug(s.get("[NAME]") + ":" + field + "=" + value);
		if(value != null && !value.isEmpty())
			s.put(field, value);
	}
	
	private void convert(Structure s, String field, Function<String, String> converter)
	{
		moveConvert(s, s, field, converter);
	}
	
	private void moveConvert(Structure s1, Structure s2, String field, Function<String, String> converter)
	{
		if(s1.containsKey(field))
		{
			String original = s1.remove(field);
			if(original != null)
			{
				set(s2, field, converter.apply(original));
			}
		}
	}
	
	private void move(Structure s1, Structure s2, String field)
	{
		if(s1.containsKey(field))
		{
			String original = s1.remove(field);
			if(original != null)
			{
				set(s2, field, original);
			}
		}
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
		return "[" + s.replace("-", ",") + "]";
	}
	
	private String convertExtrasImage(String s)
	{
		return s + ".png";
	}
	
	private String convertRatioInt(String s)
	{
		return "[" + s + "]";
	}
	
	private String convertRatioPoint(String s)
	{
		return s.replaceAll("([^,]+)", "\\[$0\\]");
	}
	
	private String convertRectangleToInsets(String s, String format)
	{
		String[] parts = s.replaceAll("\\[|\\]| ", "").split(",");
		String newValue = String.format(format, parts[1], parts[2], parts[3], parts[0]);
		newValue = StringUtils.replace(newValue, "--", "");
		newValue = StringUtils.replace(newValue, "-0", "0");
		return newValue;
	}
	
	private String convertPointToInsets(String s)
	{
		String[] parts = s.replaceAll("\\[|\\]| ", "").split(",");
		String newValue = String.format("[%s,%s,%s,%s]", parts[1], parts[0], parts[1], parts[0]);
		return newValue;
	}
	
	private String simplifyArray(String s)
	{
		return s.replaceAll(":[^,\\]]+", "").replaceAll("\\[|\\]", "");
	}
	
	private String stringArrayToSoundArray(String s)
	{
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
