# Changelog

**Format**: *W.X.Y.Z*
- W: Breaks existing command syntax
- X: Adds new command or command syntax
- Y: Changes output messages
- Z: Fixes issues

## 1.1.0.0
- Added `/home set <name> [<x> <y> <z>])` to store coordinates for teleporting.
- Added `/home get <name>` to retrieve home coordinates.
- Added `/home go <name>` to teleport to a stored home.
- Changed translation string `"command.error.invalidGamemode"` to `"command.error.gamemode.invalid"`.

## 1.0.1.0
- Added success message when changing gamemode.
- Added error message when an invalid gamemode is specified.

## 1.0.0.0
- Added `/gm <gamemode>` for changing gamemode.
- Added `/gm{0|s}` for changing gamemode to Survival.
- Added `/gm{1|c}` for changing gamemode to Creative.
- Added `/gm{2|a}` for changing gamemode to Adventure.
- Added `/gm{3|sp}` for changing gamemode to Spectator.
