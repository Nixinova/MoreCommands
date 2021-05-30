package com.nixinova.morecmds.commands;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static com.mojang.brigadier.arguments.StringArgumentType.string;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.nixinova.morecmds.Main;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.world.GameMode;

public class Gamemode {

	public void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			// Generic
			dispatcher.register(literal("gm").then(
				argument("mode", string()).executes(this::generic)
			));
			// Survival
			dispatcher.register(literal("gms").executes(this::survival));
			dispatcher.register(literal("gm0").executes(this::survival));
			// Creative
			dispatcher.register(literal("gmc").executes(this::creative));
			dispatcher.register(literal("gm1").executes(this::creative));
			// Adventure
			dispatcher.register(literal("gma").executes(this::adventure));
			dispatcher.register(literal("gm2").executes(this::adventure));
			// Spectator
			dispatcher.register(literal("gmsp").executes(this::spectator));
			dispatcher.register(literal("gm3").executes(this::spectator));
		});
	}

	private int generic(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		Main.debug("Command 'gm' activated");
		String input = StringArgumentType.getString(context, "mode");
		GameMode mode = null;
		switch (input.toLowerCase()) {
			case "survival": case "s": case "0": mode = GameMode.SURVIVAL; break;
			case "creative": case "c": case "1": mode = GameMode.CREATIVE; break;
			case "adventure": case "a": case "2": mode = GameMode.ADVENTURE; break;
			case "spectator": case "sp": case "3": mode = GameMode.SPECTATOR; break;
		}
		if (mode == null) return -1;
		context.getSource().getPlayer().setGameMode(mode);
		return 1;
	}

	private int survival(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		Main.debug("Command 'gms' activated");
		context.getSource().getPlayer().setGameMode(GameMode.SURVIVAL);
		return 1;
	}

	private int creative(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		Main.debug("Command 'gmc' activated");
		context.getSource().getPlayer().setGameMode(GameMode.CREATIVE);
		return 1;
	}

	private int adventure(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		Main.debug("Command 'gma' activated");
		context.getSource().getPlayer().setGameMode(GameMode.ADVENTURE);
		return 1;
	}

	private int spectator(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		Main.debug("Command 'gmsp' activated");
		context.getSource().getPlayer().setGameMode(GameMode.SPECTATOR);
		return 1;
	}

}
