package dialog.core;

import com.stencyl.Engine;

import openfl.display.BitmapData;
import openfl.geom.Point;
import openfl.geom.Rectangle;

import dialog.util.BitmapDataUtil;

import dialog.ds.*;

typedef StencylAnim = com.stencyl.models.actor.Animation;

class AnimatedImage
{
	public var anim:StencylAnim;
	public var width:Int;
	public var height:Int;
	public var repeats:Bool;
	public var elapsed:Int;
	public var curFrame:Int;
	public var numFrames:Int;
	public var framesAcross:Int;
	public var durations:Array<Int>;
	public var frames:Array<BitmapData>;

	public var done:Bool;

	public var curFrameImg:BitmapData;

	private static var loadedAnimations:Map<Animation, StencylAnim> = new Map<Animation, StencylAnim>();

	public function new(animRef:Animation)
	{
		if(!loadedAnimations.exists(animRef))
			loadedAnimations.set(animRef, BitmapDataUtil.getActorTypeAnimation(animRef.actor, animRef.anim));

		anim = loadedAnimations.get(animRef);
		width = Std.int(anim.imgWidth * Engine.SCALE / anim.framesAcross);
		height = Std.int(anim.imgHeight * Engine.SCALE / anim.framesDown);

		repeats = anim.looping;
		elapsed = 0;
		curFrame = 0;
		numFrames = anim.frameCount;
		framesAcross = anim.framesAcross;
		durations = anim.durations;
		frames = anim.frames;

		curFrameImg = frames[0];
	}

	public function start():Void
	{
		Dialog.get().addAnimation(this);
	}

	public function end():Void
	{
		Dialog.get().removeAnimation(this);
	}

	public function draw(x:Int, y:Int):Void
	{
		G2.drawImage(curFrameImg, x, y, false);
	}

	public function update():Void
	{
		if(done)
			return;

		elapsed += 10;
		if(elapsed >= durations[curFrame])
		{
			++curFrame;
			elapsed = 0;
			if(curFrame >= numFrames)
			{
				if(!repeats)
				{
					--curFrame;
					done = true;
				}
				else
					curFrame = 0;
			}

			curFrameImg = frames[curFrame];
		}
	}

	private static var zeroPoint:Point = new Point(0, 0);

	public function copyFrame(frame:Int):BitmapData
	{
		return frames[frame].clone();
	}
}
