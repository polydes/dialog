package dialog.ext;

import com.stencyl.models.Font;
import com.stencyl.Engine;

import nme.display.BitmapData;
import nme.geom.Point;

import dialog.core.*;
import dialog.ds.*;
import dialog.geom.*;

using dialog.util.BitmapDataUtil;

class CharacterScripts extends dialog.core.DialogExtension
{
	private var nameBitmap:BitmapData;
	private var _nameboxWindow:DialogWindow;
	private var nameboxVisible:Bool;
	private var faceBitmap:BitmapData;

	private var style:dialog.ds.ext.CharacterScripts;

	public function new()
	{
		super();
	}

	override public function setup(dg:DialogBox, style:Dynamic)
	{
		super.setup(dg, style);
		this.style = style;

		name = "Character Scripts";

		nameBitmap = null;
		_nameboxWindow = null;
		nameboxVisible = false;
		faceBitmap = null;

		cmds =
		[
			"showname"=>showname,
			"hidename"=>hidename,
			"face"=>face
		];

		addCallback(Dialog.WHEN_CREATED, function():Void
		{
			if(_nameboxWindow == null)
				_nameboxWindow = new DialogWindow(style.nameboxWindow);
			faceBitmap = null;
		});
		addCallback(Dialog.RESTORE_DEFAULTS, function():Void
		{
			faceBitmap = null;
		});
		addCallback(Dialog.ALWAYS, function():Void
		{
			_nameboxWindow.update(10);
		});
		addDrawCallback("Namebox", function():Void
		{
			if(nameboxVisible)
			{
				_nameboxWindow.draw();

				if(_nameboxWindow.tween == null)
				{
					G2.drawImage(nameBitmap, _nameboxWindow.position.x + _nameboxWindow.template.insets.left, _nameboxWindow.position.y + _nameboxWindow.template.insets.top, false);
				}
			}
		});
		addDrawCallback("Face", function():Void
		{
			if(faceBitmap != null)
			{
				var p:IntPoint = null;
				if(style.faceRelation == "Screen")
				{
					p = Util.getScreenPos(style.facePos);
				}
				else
				{
					var w:DialogWindow = dg.dgBase.getWindow();
					p = Util.getPos(style.facePos, w.size.x, w.size.y);
					p.x += w.position.x;
					p.y += w.position.y;
				}

				var origin:IntPoint = Util.getPos(style.faceOrigin, faceBitmap.width, faceBitmap.height);
				G2.drawImage(faceBitmap, p.x - origin.x, p.y - origin.y, false);
			}
		});
		addCallback(Dialog.WHEN_MESSAGE_BOX_CLEARED, function():Void
		{
			face("none");
			hidename();
		});
	}

	public function showname(nameToDraw:String):Void
	{
		nameboxVisible = true;

		var f:DialogFont = DialogFont.get(style.nameboxFont);
		nameBitmap = BitmapDataUtil.newTransparentImg(f.info.getScaledWidth(nameToDraw), f.info.scaledLineHeight + f.info.belowBase);
		_nameboxWindow.setContentSize(G2.us(nameBitmap.width), G2.us(nameBitmap.height));
		var x:Int = 0;
		for(i in 0...nameToDraw.length)
		{
			nameBitmap.drawChar(nameToDraw.charAt(i), f, x, 0);
			x += f.info.getScaledAdvance(nameToDraw.charAt(i)) + dg.dgBase.getStyle().charSpacing;
		}
		_nameboxWindow.applyTween(style.nameboxWindow.createTween);
	}

	public function hidename():Void
	{
		_nameboxWindow.tweenCompleteNotify.push(function():Void
		{
			nameboxVisible = false;
		});
		_nameboxWindow.applyTween(style.nameboxWindow.destroyTween);
	}

	public function face(facename:String):Void
	{
		if(facename == "none")
		{
			faceBitmap = null;
			dg.msgX = Std.int(dg.defaultBounds.x);
			dg.msgY = Std.int(dg.defaultBounds.y);
			dg.msgW = Std.int(dg.defaultBounds.width);
			dg.msgH = Std.int(dg.defaultBounds.height);
		}
		else
		{
			faceBitmap = Util.scaledImg(style.faceImagePrefix + facename);
			dg.msgX = Std.int(dg.defaultBounds.x + style.faceMsgOffset.left);
			dg.msgY = Std.int(dg.defaultBounds.y + style.faceMsgOffset.top);
			dg.msgW = Std.int(dg.defaultBounds.width - style.faceMsgOffset.right);
			dg.msgH = Std.int(dg.defaultBounds.height - style.faceMsgOffset.bottom);
		}
	}
}
