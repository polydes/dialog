package dialog.core;

typedef ActorImpl = com.stencyl.models.Actor;

class GlobalActorID
{
	public static var actors:Map<String, ActorImpl> = new Map<String, ActorImpl>();

	public static function get(name:String):ActorImpl
	{
		return actors.get(name);
	}

	public static function set(name:String, actor:ActorImpl):Void
	{
		actors.set(name, actor);
	}
}
