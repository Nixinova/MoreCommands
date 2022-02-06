[![Latest version](https://img.shields.io/github/v/release/Nixinova/MoreCommands?label=version&style=flat-square)](https://github.com/Nixinova/MoreCommands/releases)
[![Last updated](https://img.shields.io/github/release-date/Nixinova/MoreCommands?label=updated&style=flat-square)](https://github.com/Nixinova/MoreCommands/releases)
[![Downloads](https://cf.way2muchnoise.eu/short_487893.svg)](https://www.curseforge.com/minecraft/mc-mods/morecmds)

- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/morecmds)

# More Commands Mod

![MoreCMDs](src/main/resources/assets/morecmds/icon.png)

Adds various useful commands to the game.

## Installation

Download a version of MoreCommands for your chosen version of Minecraft from the [releases tab](https://github.com/Nixinova/MoreCommands/releases).
Versions 1.16.x and 1.17.x are supported.

This mod requires [Fabric](https://fabricmc.net/) alongside a corresponding [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api/files) version.

## Commands

### Gamemode
- `/gm <gamemode>`: Change gamemode.
- `/gms` & `/gm0`: Change gamemode to Survival.
- `/gmc` & `/gm1`: Change gamemode to Creative.
- `/gma` & `/gm2`: Change gamemode to Adventure.
- `/gmsp` & `/gm3`: Change gamemode to Spectator.

### Teleportation
- `/home set <name> <pos>`: Store coordinates to teleport to later.
- `/home get <name>`: Retrieve the coordinates a home is located at.
- `/home remove <name>`: Remove a given home.
- `/home [go] <name>`: Teleport to a given home.
- `/home list`: List all homes you have set.

### Building
- `/shape box <x> <y> <z> <block>`: Create a box out of a given block.
- `/shape cube <size> <block>`: Create a cube out of a given block.
- `/shape pyramid <size> <block>`: Create a pyramid out of a given block.
- `/shape sphere <radius> <block>`: Create a sphere out of a given block.

### Structures
- `/structure "<keywords>" [<direction>]`: Generate any in-game structure in a specified compass direction.
  - A full list of structures can be found in [structures.txt](src/main/resources/lists/structures.txt).
  - The full structure name does not need to be used.
    Entering `"village savanna house"` as the keywords retrieves a random structure from the `village/savanna/houses/` folder.
