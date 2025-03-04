# Map Editor


Revisiting this project almost a year later makes me remember all the 
pain and suffering this fucking doomed gradle buildsystem put me through. 

Here i am, on a different pc, 11 months later, reinstalling 4 different java versions again,
just to satisfy this bugged groovy compilation bullshit.

If the readme is done, ill never touch this devils spawn ever again.

## About The Project

Yet another Java Voxel Engine, built with LibGDX and the LWJGL backend.

This [tool](https://youtu.be/NfpwKs1REg0) was designed for quick and easy custom map creation for our isometric game. However, in the spirit of rapid prototyping, things got messy fast and there was no real time for proper code cleanup and refactorings.

Now that the project is over, and this software is kind of useless, pouring even more time and effort into it seems like a waste. So, here it is, in all its glory: a half-finished now abandoned map editor.

Developed as part of our school's P-Seminar project (2023/24), it was never meant to be a full-fledged game; just a [tool](https://www.youtube.com/watch?v=h_TUP2vuaDs&list=PLESF6Vbm19P2wpGKnhaIoivBvjmuTBZJp&index=1) to help the level design guys get their asses moving.

In the end, time ran out, and what we got was: stressful sleepless nights, a working map editor held together by hopes and prayers and running purely on sleep deprivation and frustration, stunning pixel art from my classmates, early Saturday work sessions, a lack of an actual game, invaluable (life-)lessons, and a whole lot of fun memories.

## Demo
### Controls
* **Esc** - Open Menu
* **WASD; Space, Ctrl** - Move Camera
* **Shift** - Sprint
* **C** - Set Camera to later in-game view position
* **L** - Rerender Voxels in case of visual bugs (like missing faces or lighting)


* Mechanics:
  - **Shift + Q** - Quick Save
  - **Shift + Z** - Undo last block-change
    - Or: Menu -> Undo
  - **Shift + Y** - Redo last block-change
    - Or: Menu -> Redo
  - **Shift + U** - Undo all block-changes at once
  - **Shift + R** - Redo all block-changes at once
  - 
* Menu:
  - **Esc** - Open/Close Menu
  - **Load Struct** - Load a .lsb file into the editor
    - Manually enter the offset, or skip the prompt, to be able to move the structure around
    - Use the **Arrow Keys** for X-Y movement
    - Use **Shift + Arrow Keys** for Z movement
  - **Save Struct** - Save the current editors contents as a .lsb file
  - **Load World** - Load a static .swi file into the editor
  - **Save World** - Save the current editor's contents as a .swi file
> [!WARNING]
> .swi files have been deprecated in favor of .lsb files, this option is only available for legacy and backwards compatibility reasons, using is strongly discouraged
  - 
    - **Empty Canvas** - <ins>**Irreversibly**</ins> deletes all blocks in the editor
    - **Undo** - Undo the last block-change (same as Shift + Z)
    - **Redo** - Redo the last block-change (same as Shift + Y)
    - **t-Screen** - Toggle the fullscreen or windowed mode
    - **Export** - Export the created structures in json format, for use in the actual game
    - **Exit** - Close the editor, offers quick-saving before closing

<video src='https://github.com/user-attachments/assets/ab90a3a2-b1a8-4162-89b8-ebb2e7a23d62' width=180></video>
<details>
<summary>The video shows a usage demo of the editor. Pay attention to the keystrokes
</summary>

Most features are demonstrated, the keybinding visualization should help better understanding the controls.

In order of appearance the following things were shown: 
1. Installation and possible compatible versions
2. Un- and Redoing block changes
    * Using the menu or the hotkeys
    * Un/Redoing all changes at once
3. Saving and loading structures
    * Loading a structure with an offset
4. The different behavior when undoing loaded structures
    * And the option to hold shift and using the menu
5. Loading a structure
    * And moving it around manually using the arrow keys
    * Canceling the import
6. Resetting the Cam position to the later in-game view
7. Refreshing the voxel rendering
8. Quicksaving 
9. Emptying the canvas
10. Exporting the World to a json file
11. Compiling the Desktop Version
12. Extending the MapEditors block set
    * By placing a blocks.json file with the additional the block palette in the Jar's root directory
</details>

### Additional Features

The MapEditor allows for the extension of the block palette, by placing a blocks.json file adjacent to the compiled Jar. This file should contain a list of all blocks, with their respective names, textures, and IDs. The editor will then automatically load the new block palette on startup.

Here is an example: 

The added textures are stitched together, to form a big spritesheet with the internal vertex faces to help with performance.
This caused the loading process to be a bit complicated, here are a few caveats at the top of my head:
* The block palette is limited to 256 blocks, as the block ID is stored in a byte
* The block palette is not saved with the world, so the block palette has to be loaded every time and synchronized with other users
* The namespace is optional and if none is defined, 'ext:' will be prepended
* The texture array can contain up to 3 path
  * Depending on the number of entries, the textures will be applied in a specific order 
  * **1**: The texture will be applied to all faces
  * **2**: The first texture will be applied to the side faces, the second one to the top and bottom face
  * **3**: The first texture will be applied to the side faces, the second one to the top face, and the third one to the bottom face
* The texture path can be relative to the Jar's root directory or an absolute path
* The type was meant to be used for the in-game physics and rendering, it is not used in the editor itself
    * Possible types are: AIR, STONE, SOIL, WOOD, GLASS, WATER, SAND, PLANT, LEAVES, WOOL, LAVA, METAL, GRAVEL

For further information, please refer to the actual implementation in 
de.grnx.mapeditor.buildableConf.ExternalBlockLoaderIterable.java
and the managing and common class:
de.grnx.mapeditor.buildableConf.Blocks.java
(Specifically the loadExternalBlocks() method)

```json
{
  "blocks": [
    {
      "name": "namespace:Deepslate",
      "texture": ["external_assets/deepslate_tiles.png"],
      "solid": true,
      "transparent": false,
      "collision": true,
      "type": "STONE"
    },
    {
      "name": "rsp:Lodestone",
      "texture": ["external_assets/lodestone_side.png","external_assets/lodestone_top.png"],
      "solid": true,
      "collision": true,
      "transparent": true,
      "type": "STONE"
    },
    {
      "name": "namespace:Reinforced Deepslate",
      "texture": ["external_assets/reinforced_deepslate_side.png","external_assets/reinforced_deepslate_top.png","external_assets/reinforced_deepslate_bottom.png"],
      "solid": true,
      "collision": true,
      "transparent": true,
      "type": "METAL"
    },
    {
      "name": "Glass",
      "texture": ["external_assets/glass.png"],
      "solid": false,
      "collision": false,
      "transparent": true,
      "type": "GLASS"
    },
    {
      "name": "Blue Terracotta",
      "texture": ["external_assets/blue_terracotta.png"],
      "solid": true,
      "collision": true,
      "transparent": false,
      "type": "STONE"
    }
  ]
}
```



## Getting Started

### Prerequisites
The default <ins>**LibGDX 1.9.11**</ins> LWJGL backend was manually replaced with Version 3 and then switched back because of compatibility issues, there might still be some artifacts left from that.

The Gradle build system provides a core module for platform-independent functionality (and for major headaches), which is then invoked by a platform-specific module. I designed the project to run exclusively on Windows desktops, but aside from minor path differences and some manual resource loading in my own code, porting to Mac or Unix should be fairly straightforward.

Due to some circular dependencies, the project is currently stuck on Java 12, with the sourceCompatibility version set to <ins>**1.7**</ins>.

After extensive testing last year, I found the sweet spot (more like i just gave up, trying to further update to newer versions): <ins>**JDK 12**</ins> paired with the more than outdated <ins>**Gradle 5.4.1**</ins>. Other combinations might work, but consider yourself warned; I tried and failed, and getting it to run on 12 by changing to 5.4.1 was both a massive achievement and an agonizing dare. After that, I simply accepted my fate and surrendered to the devilish gradle fuck.

### Installing

To get started clone this repo

```
git clone https://github.com/grn-x/MapEditor.git
```

And either use the Gradle System like usually via the console, to
* Compile the Desktop Version: ```./gradlew desktop:comp```
* Run the Desktop Version: ```./gradlew desktop:run```
* Deploy the Desktop Version ```./gradlew desktop:dist```

Or import the repo as an IntelliJ IDEA project, as the run configuration is already set up correctly



## Help

Definitely do not ask me <grnx-git@gmail.com> or write a GH-issue, i have ptsd from working on this project


## Authors

Me

And Alex who disturbed this cursed crypt best left sealed almost a year after its rightful death and got it running on his mac,
Alex if you're reading this, please open a pull request so that i can merge your efforts

## Version History and Roadmap

A working prototype was developed over the span of 2 Months and a beta Version published in March 24

In contrast to other projects of mine, i have no interest in keeping this running, so there will most likely be no further updates or bugfixes, unless requested

## License

This project is licensed under the Apache 2.0 License - see the LICENSE.md file for details

