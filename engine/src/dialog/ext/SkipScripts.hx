package dialog.ext;

import com.stencyl.behavior.Script;
import com.stencyl.models.Sound;
import com.stencyl.Input;
import com.stencyl.Data;

import dialog.core.*;

class SkipScripts extends dialog.core.DialogExtension
{
	//Communicates with TypingScripts

	private var msgSkippable:Bool;
	private var curSkipLevel:Int;
	private var soundTimer:Float;
	private var restoreSpeedTo:Float;

	private var typingScript:TypingScripts;

	public var fastSound:Array<Sound>;
	public var zoomSound:Array<Sound>;

	private var style:dialog.ds.ext.SkipScripts;

	public function new()
	{
		super();
	}

	override public function setup(dg:DialogBox, style:Dynamic)
	{
		super.setup(dg, style);
		this.style = style;

		name = "Skip Scripts";

		msgSkippable = true;
		curSkipLevel = 0;
		soundTimer = 0;
		restoreSpeedTo = 0;

		//skip levels: default, fast skip, zoom skip, instant skip

		cmds =
		[
			"skip"=>skip,
			"noskip"=>noskip
		];

		addCallback(Dialog.WHEN_CREATED, function():Void
		{
			msgSkippable = style.skippableDefault;
			typingScript = cast(dg.getExt("Typing Scripts"), TypingScripts);

			fastSound = style.fastSound;
			zoomSound = style.zoomSound;
		});
		addCallback(Dialog.ALWAYS, function():Void
		{
			if(msgSkippable)
			{
				// Make the zoom/fast sound play immediately
				if(Input.pressed(style.fastButton) || Input.pressed(style.zoomButton))
				{
					soundTimer = Math.max(style.fastSoundInterval, style.zoomSoundInterval);
				}

				if(Input.pressed(style.instantButton))
				{
					var snd:Sound = style.instantSound;
					if(snd != null)
						Script.playSound(snd);
				}
				if(Input.check(style.instantButton))
				{
					setTypingScriptSounds(null);

					if((curSkipLevel == 1 && dg.msgTypeSpeed != style.fastSpeed) ||
					   (curSkipLevel == 2 && dg.msgTypeSpeed != style.zoomSpeed) ||
					   (curSkipLevel == 3 && dg.msgTypeSpeed != 0) ||
					   (curSkipLevel == 0))
						restoreSpeedTo = dg.msgTypeSpeed;

					curSkipLevel = 3;

					dg.msgTypeSpeed = 0;

					dg.stepTimer = dg.typeDelay = 0;
				}
				else if(Input.check(style.zoomButton))
				{
					if(style.zoomSoundInterval > 0)
					{
						setTypingScriptSounds(null);

						soundTimer += 10;
						if(soundTimer >= style.zoomSoundInterval && !(dg.paused))
						{
							soundTimer = 0;
							var snd:Sound = randomSound(zoomSound);
							if(snd != null)
								Script.playSound(snd);
						}
					}
					else
						setTypingScriptSounds(zoomSound);

					if((curSkipLevel == 1 && dg.msgTypeSpeed != style.fastSpeed) ||
					   (curSkipLevel == 2 && dg.msgTypeSpeed != style.zoomSpeed) ||
					   (curSkipLevel == 3 && dg.msgTypeSpeed != 0) ||
					   (curSkipLevel == 0))
						restoreSpeedTo = dg.msgTypeSpeed;

					curSkipLevel = 2;

					dg.stepTimer = dg.typeDelay = Std.int(style.zoomSpeed * 1000);

					dg.msgTypeSpeed = style.zoomSpeed;
				}
				else if(Input.check(style.fastButton))
				{
					if(style.fastSoundInterval > 0)
					{
						setTypingScriptSounds(null);

						soundTimer += 10;
						if(!(dg.typeDelay > style.fastSpeed*1000) && soundTimer >= style.fastSoundInterval && !(dg.paused))
						{
							soundTimer = 0;
							var snd:Sound = randomSound(fastSound);
							if(snd != null)
								Script.playSound(snd);
						}
					}
					else
						setTypingScriptSounds(fastSound);

					if((curSkipLevel == 1 && dg.msgTypeSpeed != style.fastSpeed) ||
					   (curSkipLevel == 2 && dg.msgTypeSpeed != style.zoomSpeed) ||
					   (curSkipLevel == 3 && dg.msgTypeSpeed != 0) ||
					   (curSkipLevel == 0))
						restoreSpeedTo = dg.msgTypeSpeed;

					curSkipLevel = 1;

					dg.msgTypeSpeed = style.fastSpeed;
				}
				else if(curSkipLevel != 0)
				{
					soundTimer = 0;

					dg.msgTypeSpeed = restoreSpeedTo;

					setTypingScriptSounds(typingScript.getStoredTypeSounds());

					curSkipLevel = 0;

				}
			}
			else if(curSkipLevel != 0)
			{
				curSkipLevel = 0;

				dg.msgTypeSpeed = restoreSpeedTo;
				setTypingScriptSounds(typingScript.getStoredTypeSounds());
			}
		});
	}

	public function skip():Void
	{
		msgSkippable = true;
	}

	public function noskip():Void
	{
		msgSkippable = false;
		curSkipLevel = 0;
	}

	private function setTypingScriptSounds(sound:Dynamic):Void
	{
		if(TypingScripts != null)
		{
			if(sound != null)
			{
				typingScript.setTypeSoundArray(sound);
			}
			else
			{
				typingScript.setTypeSoundArray([]);
			}
		}
	}

	private function randomSound(sounds:Array<Sound>):Null<Sound>
	{
		if(sounds.length == 0)
			return null;

		return sounds[Std.random(sounds.length)];
	}

	// Member acess

	public function getCurrentSkipLevel():Int
	{
		return curSkipLevel;
	}
}
