package dialog.ext;

import com.stencyl.behavior.Script;
import com.stencyl.models.Sound;
import com.stencyl.Data;

import dialog.core.*;

class TypingScripts extends dialog.core.DialogExtension
{
	private var typeSound:Sound;
	private var stopTypeSound:Bool;
	private var typeSoundDelay = 0;
	private var curSoundDelay = 0;
	private var typeSoundArray:Array<Sound>;
	private var storedTypeSound:Array<Sound>;

	private var style:dialog.ds.ext.TypingScripts;

	public function new()
	{
		super();
	}

	override public function setup(dg:DialogBox, style:Dynamic)
	{
		super.setup(dg, style);
		this.style = style;

		name = "Typing Scripts";

		typeSound = null;
		stopTypeSound = false;
		typeSoundDelay = style.characterSkipSFX;

		cmds =
		[
			"font"=>typefont,
			"color"=>typecolor,
			"typespeed"=>typespeed,
			"typesound"=>setTypeSound,
			"soundskip"=>setTypeSoundSkip,
			"colorfont"=>colorFont
		];

		addCallback(Dialog.WHEN_CREATED, function():Void
		{
			//typeSound = style.defaultTypeSound;
			typeSoundArray = style.defaultRandomTypeSounds;
			storedTypeSound = typeSoundArray;
		});
		addCallback(Dialog.WHEN_CHAR_TYPED, function():Void
		{
			if(!stopTypeSound && (style.playTypeSoundOnSpaces || dg.msg[dg.typeIndex] != " "))
			{
				if(typeSoundArray.length > 0)
					typeSound = typeSoundArray[Std.random(typeSoundArray.length)];
				if(typeSound != null && curSoundDelay-- == 0)
				{
					Script.playSound(typeSound);
					curSoundDelay = typeSoundDelay;
				}
			}
		});
	}

	public function typefont(fontName:String):Void
	{
		dg.msgFont = DialogFont.get(Util.font(fontName));
	}

	public function typecolor(fontColor:Int):Void
	{
		dg.msgColor = fontColor;
	}

	public function typespeed(speed:Float):Void
	{
		dg.msgTypeSpeed = speed;
	}

	public function setTypeSound(sound:Dynamic):Void
	{
		if(sound == "none")
			typeSoundArray = [];
		else
		{
			if(Std.is(sound, String))
			{
				typeSoundArray = [Util.sound("" + sound)];
				storedTypeSound = typeSoundArray;
			}
			else if(Std.is(sound, Array))
			{
				typeSoundArray = sound;
				storedTypeSound = typeSoundArray;
			}
		}
	}

	public function setTypeSoundSkip(numToSkip:Int):Void
	{
		typeSoundDelay = numToSkip;
	}

	public function colorFont(fontName:String, fontColor:Int):Void
	{
		DialogFont.get(Util.font(fontName)).setColor(fontColor);
	}

	// Member access

	public function getStoredTypeSounds():Array<Sound>
	{
		return storedTypeSound;
	}

	public function setTypeSoundArray(array:Array<Sound>):Void
	{
		typeSoundArray = array;
	}
}
