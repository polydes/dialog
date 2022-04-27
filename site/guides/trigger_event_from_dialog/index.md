+++
title = "Trigger Event From Dialog"
+++

<div class="language-dialog">

This guide covers a basic way of telling Stencyl to switch to another scene by using dialog tags. The same technique can be used to supplement the Dialog Extension with many other capabilities.

We will use the [messagescene](../../reference/command_reference#messagescene) command to send a message to a behavior in the current scene.
The behavior that receives the message will have custom code to switch scenes, and the behavior should be attached to each scene which will have scene-switching dialog.

Here's how you set it all up.

### 1. Create an event

Create a scene behavior that you will have attached to each scene with dialog in it. If you already have a suitable behavior, you can use that.

> 💡 **Tip**: Don't forget to attach the behavior you've created to your scenes.

Create a "Custom Code" event.

![Add Event > Advanced > Custom Code](img1.png)

Remove the large code block, and in its place use two small code blocks. Put the following code into the two blocks.

```haxe
public function gotoScene(sceneName:String):Void {
}
```

Now you can place blocks in between the two small code blocks and those blocks will be executed when you call the gotoScene function.

![Custom function signature](img2.png)

### 2. Write the code

Place a switch to scene block in your function, and have it switch to the scene with the specified name. Also, because we need to clean up remaining dialog if we use this function, place a large code block with the following code:

```haxe
for(dialogBox in dialog.core.Dialog.dialogBoxes)
{
	dialogBox.endMessage();
}
```

> 💡 **Tip**: Use the search bar in Design Mode if you're having trouble finding any of these blocks.

![Custom function](img3.png)

### 3. Call the event from dialog

It can now be called like this in your dialog:  
`<messagescene "Name of Behavior" "gotoScene" ["Name of Scene"]>`

For example, if the behavior with the scene switching function is called "Scene Switcher", and you want to switch to a scene with the name "Scene 2", you would use this:  
`<messagescene "Scene Switcher" "gotoScene" ["Scene 2"]>`

If you want to make it easier to use, you can create a "Character" macro.

![Character Macro](img4.png)

I used + in that example, so my dialog would look like this using that macro.

![Calling character macro](img5.png)

</div>