package dialog.ext;

import com.stencyl.Engine;
import com.stencyl.behavior.Script;

import dialog.core.*;

class MessagingScripts extends dialog.core.DialogExtension
{
	public var attributes:Map<String, Dynamic>;

	public function new()
	{
		super();
	}

	override public function setup(dg:DialogBox, style:Dynamic)
	{
		super.setup(dg, style);

		name = "Messaging Scripts";

		attributes = new Map<String, Dynamic>();

		cmds =
		[
			"setattr"=>setattr,
			"getattr"=>getattr,
			"showattr"=>showattr,
			"setvar"=>setattr,
			"getvar"=>getattr,
			"showvar"=>showattr,
			"show"=>show,

			"messagescene"=>messagescene,
			"messageactor"=>messageactor,
			"addtolist"=>addtolist,
			"removefromlist"=>removefromlist,

			"say"=>say,
			"set"=>set,
			"get"=>get,
			"listset"=>listset,
			"listget"=>listget
		];
	}

	public function analyzeAttr(source:Array<Dynamic>):Dynamic
	{
		switch("" + source[0])
		{
			case "game", "global":
				return Script.getGameAttribute("" + source[1]);
			case "dialog":
				return attributes.get("" + source[1]);
			case "actorbhv", "go":
				return GlobalActorID.get("" + source[1]).getValue("" + source[2], "" + source[3]);
			case "scenebhv":
				return Script.getValueForScene("" + source[1], "" + source[2]);
			case "actor":
				return GlobalActorID.get("" + source[1]).getActorValue("" + source[2]);
		}
		return null;
	}

	public function setattr(args:Array<Dynamic>):Void
	{
		switch("" + args[0])
		{
			case "game", "global":
				Script.setGameAttribute("" + args[1], args[2]);
			case "dialog":
				attributes.set("" + args[1], args[2]);
			case "actorbhv", "go":
				GlobalActorID.get("" + args[1]).setValue("" + args[2], "" + args[3], args[4]);
			case "scenebhv":
				Script.setValueForScene("" + args[1], "" + args[2], args[3]);
			case "actor":
				GlobalActorID.get("" + args[1]).setActorValue("" + args[2], args[3]);
		}
	}

	public function getattr(source:Array<Dynamic>):Dynamic
	{
		return analyzeAttr(source);
	}

	public function showattr(source:Array<Dynamic>):Void
	{
		show(analyzeAttr(source));
	}

	public function show(object:Dynamic):Void
	{
		dg.insertMessage("" + object);
	}

	public function addtolist(source:Array<Dynamic>):Void
	{
		analyzeAttr(source).push(source.pop());
	}

	public function removefromlist(source:Array<Dynamic>):Void
	{
		analyzeAttr(source).remove(source.pop());
	}

	public function listset(source:Array<Dynamic>, index:Int, value:Dynamic):Void
	{
		source[index] = value;
	}

	public function listget(source:Array<Dynamic>, index:Int):Dynamic
	{
		return source[index];
	}

	public function code(expr:String):Dynamic
	{
		//TODO
		return null;
	}

	public function say(object:Dynamic, message:String, args:Array<Dynamic>):Dynamic
	{
		return Reflect.callMethod(object, Reflect.field(object, message), args);
	}

	public function set(object:Dynamic, field:String, value:Dynamic):Void
	{
		Reflect.setField(object, field, value);
	}

	public function get(object:Dynamic, field:String):Dynamic
	{
		return Reflect.field(object, field);
	}

	public function messagescene(behaviorName:String, messageName:String, args:Array<Dynamic>):Void
	{
		if(behaviorName == "all")
			Script.shoutToScene(messageName, args);
		else
			Script.sayToScene(behaviorName, messageName, args);
	}

	public function messageactor(actorname:String, behaviorName:String, messageName:String, args:Array<Dynamic>):Void
	{
		if(behaviorName == "all")
			GlobalActorID.get(actorname).shout(messageName, args);
		else
			GlobalActorID.get(actorname).say(behaviorName, messageName, args);
	}
}
