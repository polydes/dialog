package com.polydes.dialog.updates;

import com.polydes.datastruct.DataStructuresExtension;

import stencyl.core.util.Lang;
import stencyl.core.util.Worker;

public class DS_V4_FullTypeNamesUpdate implements Worker
{
	@Override
	public void doWork()
	{
		DataStructuresExtension.get().getTypenameUpdater().addTypes(Lang.hashmap(
			"Animation", "dialog.core.Animation",
			"RatioInt", "dialog.geom.RatioInt",
			"RatioPoint", "dialog.geom.RatioPoint",
			"Point", "openfl.geom.Point",
			"Rectangle", "openfl.geom.Rectangle",
			"Window", "dialog.ds.WindowTemplate",
			"Tween", "dialog.ds.TweenTemplate",
			"Style", "dialog.ds.Style",
			"ScalingImage", "dialog.ds.ScalingImageTemplate"
		));
	}
}
