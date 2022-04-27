# Development Guide

## Principles

This extension has the following goals:

- **Customizability**: Allow all the surface level aspects of dialog, such as graphics, sounds, message speed, and controls, to be customized.

- **Extensibility**: If the given degree of customizability isn't enough, further allow the dialog system to be extended with new code.

- **User friendliness**: Provide an easy to use, easy to understand, and helpful graphical interface for all aspects of creating dialog.

## Architecture

In order to meet the goals listed in [Principles](#Principles), this extension is composed of numerous parts.

First, an engine extension is required for the basic functionality of displaying dialog in-game.

Second, in order to integrate directly with the Stencyl toolset and provide a friendlier interface, a toolset extension is used.

This extension is *itself* designed to be **extensible**. The core supports high-level abstractions such as the dialog data format, macros, commands, styles, tweening, and rendering dialog boxes. But **dialog behaviors** are responsible for actually defining how a dialog box looks and behaves, and what commands are available for dialog and event scripting. Of course, enough default dialog behaviors are included so that this extension can be used out of the box.

This extension is also designed to be **customizable**. In order to achieve a high degree of customizability with a friendly interface, the [Data Structures](https://github.com/polydes/structures) extension is used for the storing and editing of custom data related to dialog behaviors and dialog styles.

## Repository Structure

#### Toolset Extension

- `libs/`: third-party java libraries
- `res/`: icons and other embedded resources
- `src/`: Java source code
- `.classpath`/`.project`: Eclipse IDE metadata
- `build.xml`: Ant build script

#### Engine Extension

- `engine/`: the complete engine extension
- `engine/def/`: structure definitions for core dialog components
- `engine/def/ext`: structure definitions for bundled dialog plugins
- `engine/types`: data type definitions

#### Documentation

- `docs/`: developer documentation
- `guides/`: guides that are meant to be viewed online at dialogextension.com
- `samples/`: sample games using the dialog extension