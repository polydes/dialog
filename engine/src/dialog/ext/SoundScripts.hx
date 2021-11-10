package dialog.ext;

import com.stencyl.behavior.Script;

import dialog.core.*;

class SoundScripts extends dialog.core.DialogExtension
{
	public function new()
	{
		super();
	}

	override public function setup(dg:DialogBox, style:Dynamic)
	{
		super.setup(dg, style);

		name = "Sound Scripts";

		cmds =
		[
			"playsound"=>playsound,
			"loopsound"=>loopsound,
			"stopsound"=>stopsound,
			"playchan"=>playchan,
			"loopchan"=>loopchan,
			"stopchan"=>stopchan
		];
	}

	public function playsound(sound:String):Void
	{
		Script.playSound(Util.sound(sound));
	}

	public function loopsound(sound:String):Void
	{
		Script.loopSound(Util.sound(sound));
	}

	public function stopsound():Void
	{
		Script.stopAllSounds();
	}

	public function playchan(sound:String, channel:Int):Void
	{
		Script.playSoundOnChannel(Util.sound(sound), channel);
	}

	public function loopchan(sound:String, channel:Int):Void
	{
		Script.loopSoundOnChannel(Util.sound(sound), channel);
	}

	public function stopchan(channel:Int):Void
	{
		Script.stopSoundOnChannel(channel);
	}
}
