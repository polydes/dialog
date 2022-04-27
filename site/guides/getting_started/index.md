+++
title = "Getting Started"
+++

## Initial Setup

See the [installation guide](../../install) for intructions to install and enable the extension.

For easier initial setup, you can copy all of the settings of "Dialog Extension Sample" to your game.

1. Download [Dialog Extension Sample](/3.4.0-extensions/sample-games/dialog-extension-sample.stencyl) and import it into Stencyl.

2. Open your workspace (`stencylworks/`) from within Stencyl by going to **Tools > View Folder > Workspace**

3. With your game closed, copy the content of `stencylworks/games/Dialog Extension Sample/extras` into `stencylworks/games/[Your Game]/extras`
   ```
   stencylworks
   └─games
     ├─Dialog Extension Sample
     │ └─extras               ───┐
     └─{your own game}           │copy
       └─extras              <───┘
   ```

4. Open your game in Stencyl.

> ⚠ **Warning**:
> This brings most of the settings of the sample game into yours, with a few exceptions. These need to be set to resources in your Stencyl game:
> - Sounds
> - The "pointer" Actor Type
> - Controls

## Using the Extension

Open your game in Stencyl.

On the sidebar in the Dashboard, there are two items under "Extensions" that we will use.

- `Data Structures`: Most of the preferences related to dialog presentation can be edited here.
- `Dialog Extension`: The scripts used to display dialog can be written here.

To display your dialog in the game, some custom blocks are included:

The following blocks use the name of a dialog script to start dialog.

```
- show dialog [ ... ]
- show dialog [ ... ] and trigger event [ ... ] upon completion
- show dialog [ ... ] with style [ ... ]
- show dialog [ ... ] with style [ ... ] and trigger event [ ... ] upon completion
```

- The field after "show dialog" is filled with the name of the Dialog Chunk you wish to display.
- If one of the no-style blocks is used, the default "base" style is shown.
- If a "trigger event" block is used, once the dialog is ended with the `<end>` tag, the specified Custom Event is called.

There are four alternate blocks, `show text [ ... ] ...` which you can use to call a dialog with text from an attribute.

```dialog
// You can place text directly into the block,         //
// and it will be converted into a dialog script.      //
show text [Hi Lenny!<but> What's up?<but><end>]

// You can also store the text into an attribute,      //
// and place the attribute into the "show text" block. //
set My Dialog Attribute to [Hi Lenny!<but> What's up?<but><end>]
show text [My Dialog Attribute]
```

In addition, you can set up a Game Attribute to always be `true` if a Dialog is open, which can be used for game rules such as disallowing player movement during dialog.  
The setting for this is in `Data Structures > Default Style > Basic Settings > Control Attribute`. Just set it equal to the name of the Game Attribute you want set to true while Dialog is open.

## Documentation

It's a good idea to install the included "Dialog Extension Sample" and see how it works. Look through the settings for the Dialog Extension and Data Structures, and you can see how dialog is shown in the events for "Good Scene."

See the [command reference](../../reference/command_reference) for information on using the Dialog Extension's scripting capabilities.