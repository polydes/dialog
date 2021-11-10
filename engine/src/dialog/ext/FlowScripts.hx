package dialog.ext;

import com.stencyl.models.Actor;
import com.stencyl.models.Sound;
import com.stencyl.behavior.Script;
import com.stencyl.Engine;
import com.stencyl.Input;

import dialog.core.*;
import dialog.ds.*;

class FlowScripts extends dialog.core.DialogExtension
{
	//Communicates with SkipScripts

	private var waitingForAttribute:Bool;
	private var waitElapsed:Int;
	private var source:Array<Dynamic>;
	private var clearAfterInput:Bool;
	private var pointer:AnimatedImage;

	public var noInputSoundWithTags:Array<String>;

	private var skipScripts:SkipScripts;

	private var style:dialog.ds.ext.FlowScripts;

	public function new()
	{
		super();
	}

	override public function setup(dg:DialogBox, style:Dynamic)
	{
		super.setup(dg, style);
		this.style = style;

		name = "Flow Scripts";

		waitingForAttribute = false;
		waitElapsed = 0;
		source = new Array<Dynamic>();
		clearAfterInput = false;
		pointer = null;

		cmds =
		[
			"waitattr"=>waitattr,
			"waitvar"=>waitattr,
			"wait"=>wait,
			"but"=>but,
			"bc"=>bc
		];

		addCallback(Dialog.WHEN_CREATED, function():Void
		{
			skipScripts = cast(dg.getExt("Skip Scripts"), SkipScripts);
			noInputSoundWithTags = [];
			if(style.noInputSoundWithTags != null)
				for(tagname in (style.noInputSoundWithTags: Array<String>))
					noInputSoundWithTags.push("" + tagname);
		});
		addCallback(Dialog.ALWAYS, function():Void
		{
			if(waitingForAttribute)
			{
				var done:Bool = analyzeAttr(source) == source[source.length - 1];
				if(done)
				{
					waitingForAttribute = false;
					dg.paused = false;
				}
			}
			if(pointer != null)
			{
				if(style.waitingSoundInterval > 0)
				{
					waitElapsed += Engine.STEP_SIZE;

					if(waitElapsed >= style.waitingSoundInterval)
					{
						waitElapsed = 0;
						var snd:Sound = style.waitingSound;
						if(snd != null)
							SoundManager.playSound(snd);
					}
				}

				if(Input.pressed(style.advanceDialogButton) || skipLevel(2))
				{
					dg.paused = false;
					pointer.end();
					pointer = null;
					if(clearAfterInput)
					{
						dg.clearMessage();
						clearAfterInput = false;
					}

					waitElapsed = 0;

					if(!skipLevel(2) && !nearCancelingTag())
					{
						var snd:Sound = style.inputSound;
						if(snd != null)
							SoundManager.playSound(snd);
					}
				}
			}
		});
		addDrawCallback("Wait Pointer", function():Void
		{
			if(pointer == null)
				return;

			pointer.draw(Std.int(style.pointerPos.xp * Engine.screenWidth + style.pointerPos.xv), Std.int(style.pointerPos.yp * Engine.screenHeight + style.pointerPos.yv));
		});
	}

	//copied from MessagingScripts
	public function analyzeAttr(source:Array<Dynamic>):Dynamic
	{
		switch("" + source[0])
		{
			case "game", "global":
				return Script.getGameAttribute("" + source[1]);
			case "actorbhv", "go":
				return GlobalActorID.get("" + source[1]).getValue("" + source[2], "" + source[3]);
			case "scenebhv":
				return Script.getValueForScene("" + source[1], "" + source[2]);
			case "actor":
				return GlobalActorID.get("" + source[1]).getActorValue("" + source[2]);
		}
		return null;
	}

	public function waitattr(source:Array<Dynamic>):Void
	{
		dg.paused = true;
		waitingForAttribute = true;
		this.source = source;
	}

	public function wait(duration:Float):Void
	{
		dg.typeDelay = Std.int(duration * 1000);
	}

	public function but():Void
	{
		dg.paused = true;
		pointer = new AnimatedImage(style.animForPointer);
		pointer.start();

		if(!skipLevel(2))
		{
			var snd:Sound = style.waitingSound;
			if(snd != null)
				SoundManager.playSound(snd);
		}
	}

	public function bc():Void
	{
		but();
		clearAfterInput = true;
	}

	private function nearCancelingTag():Bool
	{
		var cancelTagFound:Bool = false;

		var i:Int = dg.typeIndex + 1;
		while(i < dg.msg.length)
		{
			if(Std.is(dg.msg[i], String))
			{
				var s:String = dg.msg[i];
				if(!(s == " " || s == "\r" || s == "\n" || s == "\t"))
					return false;
			}
			else
			{
				var cmdName = cast(dg.msg[i], Tag).name;

				if(cmdName == "wait")
					return false;

				for(cancelTag in noInputSoundWithTags)
				{
					if(cmdName == cancelTag)
					{
						return true;
					}
				}
			}
			++i;
		}

		return false;
	}

	private function skipLevel(level:Int):Bool
	{
		if(skipScripts == null)
			return false;

		return skipScripts.getCurrentSkipLevel() == level;
	}
}
