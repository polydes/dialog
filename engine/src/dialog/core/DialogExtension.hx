package dialog.core;

import dialog.ds.*;

@:keepSub
class DialogExtension
{
	private var dg:DialogBox;

	public var name:String;

	public var cmds:Map<String, Dynamic>; //cmdName, <Function>
	public var callbacks:Map<Int, Array<Void->Void>>; //callbackConstant, <Array> //id, <Function>
	public var graphicsCallbacks:Map<String, Void->Void>;

	public function new()
	{
	}

	public function setup(dg:DialogBox, style:Dynamic)
	{
		this.dg = dg;

		cmds = new Map<String, Dynamic>();
		callbacks = new Map<Int, Array<Void->Void>>();
		graphicsCallbacks = new Map<String, Void->Void>();
	}

	private function addCallback(callbackConst:Int, f:Void->Void):Void
	{
		if(!(callbacks.exists(callbackConst)))
		{
			callbacks.set(callbackConst, new Array<Void->Void>());
		}
		callbacks.get(callbackConst).push(f);
	}

	private function addDrawCallback(callbackName:String, f:Void->Void):Void
	{
		graphicsCallbacks.set(callbackName, f);
	}
}
