package dialog.core;

import com.stencyl.models.actor.ActorType;
import com.stencyl.models.actor.Sprite;
import com.stencyl.models.Font;
import com.stencyl.models.Resource;
import com.stencyl.models.Sound;
import com.stencyl.Data;
import com.stencyl.Engine;

import openfl.Assets;
import openfl.display.BitmapData;
import openfl.geom.Point;

using dialog.util.BitmapDataUtil;

import dialog.ds.*;
import dialog.geom.*;

class Util
{
	public static function sound(name:String):Sound
	{
		var o:Sound = null;

		o = cast Data.get().resourceMap.get(name);

		if(o == null)
		{
			trace("Failed to load Resource: " + name);
		}

		return o;
	}

	public static function font(name:String):Font
	{
		var o:Font = null;

		o = cast Data.get().resourceMap.get(name);

		if(o == null)
		{
			trace("Failed to load Resource: " + name);
		}

		return o;
	}

	public static function img(name:String):BitmapData
	{
		var o:BitmapData = null;

		o = Assets.getBitmapData("assets/data/com.polydes.dialog/images/"+ name +".png");

		if(o == null)
		{
			trace("Failed to load Resource: " + name);
		}

		return o;
	}

	public static function scaledImg(name:String):BitmapData
	{
		var o:BitmapData = null;

		if(Engine.IMG_BASE == "1x")
			o = Assets.getBitmapData("assets/data/com.polydes.dialog/images/"+ name +".png");
		else
		{
			var toReturn = Assets.getBitmapData("assets/data/com.polydes.dialog/images/"+ name + "@" + Engine.IMG_BASE + ".png");
			if(toReturn == null)
				toReturn = Assets.getBitmapData("assets/data/com.polydes.dialog/images/"+ name +".png").getScaled(Engine.SCALE, Engine.SCALE);
			o = toReturn;
		}

		if(o == null)
		{
			trace("Failed to load Resource: " + name);
		}

		return o;
	}

	public static function text(name:String):String
	{
		var o:String = null;

		o = Assets.getText("assets/data/com.polydes.dialog/"+ name);
		if(o == null)
			trace("Failed to load Resource: " + name);
		else
			o = convertFromPseudoUnicode(o);

		return o;
	}
	
	public static function convertFromPseudoUnicode(text:String):String
	{
		var index = 0;
		var lastIndex = 0;
		var sb:StringBuf = null;
		
		while((index = text.indexOf("~x", lastIndex)) != -1)
		{
			if(sb == null) sb = new StringBuf();
			sb.addSub(text, lastIndex, (index - lastIndex));
			var codepoint = Std.parseInt("0x"+text.substring(index + 2, index + 6));
			sb.addChar(codepoint);
			lastIndex = index + 6;
		}
		if(sb != null)
		{
			if(lastIndex < text.length)
				sb.addSub(text, lastIndex, (text.length - lastIndex));
			return sb.toString();
		}
		
		return text;
	}

	public static function getTitleIndices(s_split:Array<String>, titleMarker:String):Array<Int>
	{
		var indices:Array<Int> = new Array<Int>();

		for(i in 0...s_split.length)
		{
			if(s_split[i].charAt(0) == titleMarker)
				indices.push(i);
		}

		return indices;
	}

	public static function valueOfString(s:String):Dynamic
	{
		//Hexadicimal Int
		if(s.substring(0, 2) == "0x")
		{
			return Std.parseInt(s);
		}

		if(s.substring(0, 1) == "#")
		{
			return Std.parseInt("0x" + s.substr(1));
		}

		//Float or Int
		if(!Math.isNaN(Std.parseFloat(s)))
			return Std.parseFloat(s);

		//Bool
		if(s == "true")
			return true;

		if(s == "false")
			return false;

		return s;
	}

	public static function trim(s:String):String
	{
		if(s.charAt(0) != " " && s.charAt(s.length - 1) != " ")
			return s;

		var a:Array<String> = s.split("");

		while(a[0] == " ")
			a.shift();
		while(a[a.length - 1] == " ")
			a.pop();

		return a.join("");
	}

	public static var newlinePattern:EReg = ~/[\r\n]+/g;

	public static function getLines(s:String):Array<String>
	{
		return newlinePattern.split(s);
	}

	public static function getFileLines(filename:String):Array<String>
	{
		return newlinePattern.split(text(filename));
	}

	public static function getX(i:RatioInt, base:Int):Int
	{
		return Std.int(i.v + i.p * base);
	}

	public static function getY(i:RatioInt, base:Int):Int
	{
		return Std.int(i.v + i.p * base);
	}

	public static function getScreenPos(point:RatioPoint):IntPoint
	{
		return new IntPoint(point.xv + point.xp * Engine.screenWidth, point.yv + point.yp * Engine.screenHeight);
	}

	public static function getPos(point:RatioPoint, w:Int, h:Int):IntPoint
	{
		return new IntPoint(point.xv + point.xp * w, point.yv + point.yp * h);
	}
}
