package dialog.core;

import com.stencyl.behavior.Script;
import com.stencyl.models.Sound;

class SoundManager
{
	public static function playSound(clip:Sound)
	{
		GlobalActorID.get("Sound Manager Actor").say("Sound Manager Behavior", "playSound", [clip]);
	}
}
