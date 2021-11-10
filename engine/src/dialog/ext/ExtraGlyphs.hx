package dialog.ext;

import openfl.display.BitmapData;
import openfl.geom.Point;

import com.stencyl.Engine;

import dialog.core.*;

using dialog.util.BitmapDataUtil;

class ExtraGlyphs extends dialog.core.DialogExtension
{
	private var style:dialog.ds.ext.ExtraGlyphs;

	public function new()
	{
		super();
	}

	override public function setup(dg:DialogBox, style:Dynamic)
	{
		super.setup(dg, style);
		this.style = style;

		name = "Extra Glyphs";

		cmds =
		[
			"glyph"=>glyph
		];
	}

	public function glyph(glyphName:String):Void
	{
		var img:BitmapData = Util.scaledImg(glyphName);

		if(dg.drawX + img.width > dg.msgW)
			dg.startNextLine();
		if(dg.drawHandler != null)
		{
			var charID:Int = dg.drawHandler.addImg(img, G2.s(dg.msgX + dg.drawX), G2.s(dg.curLine.pos.y + dg.curLine.aboveBase) - img.height, false);
			dg.curLine.drawHandledChars.push(new DrawHandledImage(dg.drawHandler, charID));
		}
		else
			dg.curLine.img.drawImage(img, G2.s(dg.drawX), G2.s(dg.curLine.aboveBase) - img.height);
		dg.drawX += G2.us(img.width) + style.glyphPadding;
		dg.typeDelay = Std.int(dg.msgTypeSpeed * 1000);
		dg.runCallbacks(Dialog.WHEN_CHAR_TYPED);
	}
}
