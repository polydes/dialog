package dialog.util;

import openfl.display.*;
import openfl.geom.*;

import com.stencyl.graphics.G;
import com.stencyl.models.actor.ActorType;
import com.stencyl.models.actor.Animation;
import com.stencyl.models.actor.Sprite;
import com.stencyl.models.Font;
import com.stencyl.Data;
import com.stencyl.Engine;

import dialog.core.DialogFont;
import dialog.geom.*;

using dialog.util.BitmapDataUtil;

class BitmapDataUtil
{
	public static function get9Scaled(src:BitmapData, width:Int, height:Int, insets:Insets, stretch:Bool):BitmapData
	{
		if(width <= 0) width = src.width;
		if(height <= 0) height = src.height;

		var inl = Std.int(insets.left);
		var inr = Std.int(insets.right);
		var int = Std.int(insets.top);
		var inb = Std.int(insets.bottom);

		if(inl <= 0) inl = 1;
		if(inr <= 0) inr = 1;
		if(int <= 0) int = 1;
		if(inb <= 0) inb = 1;

		var bg:BitmapData = new BitmapData(width, height);
		var w:Int = src.width;
		var h:Int = src.height;
		var mW:Int = w - inl - inr;
		var mH:Int = h - int - inb;
		var p:Point = new Point(0, 0);
		var rect:Rectangle = new Rectangle(0, 0, 0, 0);

		//copy corners
		rect.width = inl;
		rect.height = int;
		//top left
		rect.x = 0;
		rect.y = 0;
		bg.copyPixels(src, rect, p);
		//top right
		rect.x = w - inr;
		rect.width = inr;
		p.x = width - inr;
		bg.copyPixels(src, rect, p);
		//bottom right
		rect.y = h - inb;
		rect.height = inb;
		p.y = height - inb;
		bg.copyPixels(src, rect, p);
		//bottom left
		rect.x = 0;
		rect.width = inl;
		p.x = 0;
		bg.copyPixels(src, rect, p);

		//copy edges
		var newRect:Rectangle = new Rectangle(0, 0, 0, 0);
		var subWidth = (width - inl - inr);
		var subHeight = (height - int - inb);
		//top and bottom edges
		newRect.x = 0;
		newRect.y = 0;
		newRect.width = subWidth;
		newRect.height = int;
		rect.x = inl;
		rect.y = 0;
		rect.width = mW;
		rect.height = int;
		p.x = inl;
		p.y = 0;
		bg.copyPixels(src.getDimScaledPartial(rect, subWidth, int), newRect, p);
		newRect.height = inb;
		rect.y = h - inb;
		rect.height = inb;
		p.y = height - inb;
		bg.copyPixels(src.getDimScaledPartial(rect, subWidth, inb), newRect, p);
		//left and right edges
		newRect.width = inl;
		newRect.height = subHeight;
		rect.x = 0;
		rect.y = int;
		rect.width = inl;
		rect.height = mH;
		p.x = 0;
		p.y = int;
		bg.copyPixels(src.getDimScaledPartial(rect, inl, subHeight), newRect, p);
		newRect.width = inr;
		rect.x = w - inr;
		rect.width = inr;
		p.x = width - inr;
		bg.copyPixels(src.getDimScaledPartial(rect, inr, subHeight), newRect, p);

		//copy center
		newRect.width = subWidth;
		newRect.height = subHeight;
		rect.x = inl;
		rect.y = int;
		rect.width = mW;
		rect.height = mH;
		p.x = inl;
		p.y = int;
		
		if(stretch)
			bg.copyPixels(src.getDimScaledPartial(rect, subWidth, subHeight), newRect, p);
		else
			bg.copyPixels(src.getDimTiledPartial(rect, subWidth, subHeight), newRect, p);

		return bg;
	}

	public static function getPartial(src:BitmapData, rect:Rectangle):BitmapData
	{
		var newImg:BitmapData = new BitmapData(Std.int(rect.width), Std.int(rect.height));
		newImg.copyPixels(src, rect, zeroPoint);
		return newImg;

		//TODO: This may be buggy (swapping y pixels?)
		//return TextureUtil.getSubTexture(src, rect);
	}

	//specifying scale by target size

	public static function getDimScaled(src:BitmapData, newWidth:Int, newHeight:Int):BitmapData
	{
		var sX = newWidth / src.width;
		var sY = newHeight / src.height;
		
		if(newWidth <= 0 || newHeight <= 0)
		{
			return new BitmapData(1, 1, true, 0);
		}

		var newImg:BitmapData = new BitmapData(newWidth, newHeight, true, 0);

		var matrix:Matrix = new Matrix();
		matrix.scale(sX, sY);
		newImg.draw(src, matrix);
		return newImg;
	}

	public static function getDimTiled(src:BitmapData, newWidth:Int, newHeight:Int):BitmapData
	{
		var sX = newWidth / src.width;
		var sY = newHeight / src.height;
		
		var tilesX:Int = Math.ceil(sX);
		var tilesY:Int = Math.ceil(sY);

		var newImg:BitmapData = new BitmapData(newWidth, newHeight, true, 0);
		var matrix:Matrix = new Matrix();
		for(y in 0...tilesY)
		{
			for(x in 0...tilesX)
			{
				newImg.draw(src, matrix);
				matrix.translate(src.width, 0);
			}
			matrix.translate(src.width * (-tilesX), src.height);
		}

		return newImg;
	}

	public static function getDimScaledPartial(src:BitmapData, rect:Rectangle, newWidth:Int, newHeight:Int):BitmapData
	{
		return src.getPartial(rect).getDimScaled(newWidth, newHeight);
	}

	public static function getDimTiledPartial(src:BitmapData, rect:Rectangle, newWidth:Int, newHeight:Int):BitmapData
	{
		return src.getPartial(rect).getDimTiled(newWidth, newHeight);
	}

	//specifying scale by multiple

	public static function getScaled(src:BitmapData, sX:Float, sY:Float):BitmapData
	{
		var newWidth = Std.int(src.width * sX);
		var newHeight = Std.int(src.height * sY);

		if(newWidth <= 0 || newHeight <= 0)
		{
			return new BitmapData(1, 1, true, 0);
		}

		var newImg:BitmapData = new BitmapData(newWidth, newHeight, true, 0);

		var matrix:Matrix = new Matrix();
		matrix.scale(sX, sY);
		newImg.draw(src, matrix);
		return newImg;
	}

	public static function getTiled(src:BitmapData, sX:Float, sY:Float):BitmapData
	{
		var tilesX:Int = Math.ceil(sX);
		var tilesY:Int = Math.ceil(sY);

		var newImg:BitmapData = new BitmapData(Std.int(src.width * sX), Std.int(src.height * sY), true, 0);
		var matrix:Matrix = new Matrix();
		for(y in 0...tilesY)
		{
			for(x in 0...tilesX)
			{
				newImg.draw(src, matrix);
				matrix.translate(src.width, 0);
			}
			matrix.translate(src.width * (-tilesX), src.height);
		}

		return newImg;
	}

	public static function getScaledPartial(src:BitmapData, rect:Rectangle, sX:Float, sY:Float):BitmapData
	{
		return src.getPartial(rect).getScaled(sX, sY);
	}

	public static function getTiledPartial(src:BitmapData, rect:Rectangle, sX:Float, sY:Float):BitmapData
	{
		return src.getPartial(rect).getTiled(sX, sY);
	}

	public static function drawChar(img:BitmapData, c:String, font:DialogFont, x:Int, y:Int):Void
	{
		var src:BitmapData = font.getScaledChar(c);
		var offset = font.info.getScaledOffset(c);
		img.drawImage(src, cast x + offset.x, cast y + offset.y);
	}

	public static function drawImage(img:BitmapData, brush:BitmapData, x:Int, y:Int):Void
	{
		img.copyPixels(brush, brush.rect, new Point(x, y));
	}

	public static function newTransparentImg(w:Int, h:Int):BitmapData
	{
		var bmd = new BitmapData(w, h true, 0);
		return bmd;
	}

	public static function getImagesFromAnimation(type:ActorType, animName:String):Array<BitmapData>
	{
		var sprite:Sprite = cast(Data.get().resources.get(type.spriteID), Sprite);
		var a:Animation = null;
		for(i in sprite.animations.keys())
		{
			if(sprite.animations.get(i) == null) continue;
			if(cast(sprite.animations.get(i), Animation).animName == animName)
			{
				a = cast(sprite.animations.get(i), Animation);
			}
		}
		if(a == null) return null;
		if(!a.graphicsLoaded)
			a.loadGraphics();
		
		return a.frames;
	}

	public static function getActorTypeAnimation(type:ActorType, animName:String):Animation
	{
		var sprite:Sprite = cast(Data.get().resources.get(type.spriteID), Sprite);
		var a:Animation = null;
		for(i in sprite.animations.keys())
		{
			if(sprite.animations.get(i) == null) continue;
			if(cast(sprite.animations.get(i), Animation).animName == animName)
			{
				a = cast(sprite.animations.get(i), Animation);
			}
		}

		return a;
	}

	public static function asBitmapData(o:Dynamic):BitmapData
	{
		return cast(o, BitmapData);
	}

	private static var zeroPoint = new Point(0, 0);
}
