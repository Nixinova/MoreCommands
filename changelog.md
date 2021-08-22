# Changelog

**Format**: *W.X.Y.Z*
- W: Breaks existing command syntax
- X: Adds new command or command syntax
- Y: Changes command suggestions or output
- Z: Fixes issues

## 1.3.0.0
- Added `/shape box <x> <y> <z> <block>` to create a box out of a given block.
- Added `/shape cube <size> <block>` to create a cube out of a given block.
- Added `/shape pyramid <size> <block>` to create a pyramid out of a given block.
- Added `/shape sphere <radius> <block>` to create a sphere out of a given block.

## 1.2.1.0
- Added autocomplete support for coordinates in `/home set`.

## 1.2.0.1
- Fixed permissions not being fully followed for `/home` commands.

## 1.2.0.0
- Added `/home list` which lists all homes the player currently has set.
- Added `/home remove` which removes a set home.

## 1.1.1.0
- Added autofill support to `/gm`.
- Changed commands to fail if the executor lacks necessary permissions.

## 1.1.0.0
- Added `/home set <name> <x> <y> <z>` to store coordinates for teleporting.
- Added `/home get <name>` to retrieve home coordinates.
- Added `/home go <name>` to teleport to a stored home.

## 1.0.1.0
- Added success message when changing gamemode.
- Added error message when an invalid gamemode is specified.

## 1.0.0.0
- Added `/gm <gamemode>` for changing gamemode.
- Added `/gm{0|s}` for changing gamemode to Survival.
- Added `/gm{1|c}` for changing gamemode to Creative.
- Added `/gm{2|a}` for changing gamemode to Adventure.
- Added `/gm{3|sp}` for changing gamemode to Spectator.
