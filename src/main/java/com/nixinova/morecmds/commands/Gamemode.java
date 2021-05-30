package com.nixinova.morecmds.commands;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static com.mojang.brigadier.arguments.StringArgumentType.string;

import com.nixinova.morecmds.Main;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.world.GameMode;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

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
		Main.log("Command 'gm' activated");
		String modeInput = StringArgumentType.getString(context, "mode");

		ServerPlayerEntity player = context.getSource().getPlayer();
		GameMode mode = null;
		switch (modeInput.toLowerCase()) {
			case "survival": case "s": case "0": mode = GameMode.SURVIVAL; break;
			case "creative": case "c": case "1": mode = GameMode.CREATIVE; break;
			case "adventure": case "a": case "2": mode = GameMode.ADVENTURE; break;
			case "spectator": case "sp": case "3": mode = GameMode.SPECTATOR; break;
		}
		if (mode == null) {
			sendMessage(player, false, modeInput);
			TranslatableText invalid = new TranslatableText("command.error.gamemode.invalid");
			throw new SimpleCommandExceptionType(invalid).create();
		}

		player.setGameMode(mode);
		return Command.SINGLE_SUCCESS;
	}

	private int survival(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		Main.log("Command 'gms' activated");
		ServerPlayerEntity player = context.getSource().getPlayer();
		player.setGameMode(GameMode.SURVIVAL);
		Gamemode.sendMessage(player, true, "Survival");
		return Command.SINGLE_SUCCESS;
	}

	private int creative(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		Main.log("Command 'gmc' activated");
		ServerPlayerEntity player = context.getSource().getPlayer();
		player.setGameMode(GameMode.CREATIVE);
		Gamemode.sendMessage(player, true, "Creative");
		return Command.SINGLE_SUCCESS;
	}

	private int adventure(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		Main.log("Command 'gma' activated");
		ServerPlayerEntity player = context.getSource().getPlayer();
		player.setGameMode(GameMode.ADVENTURE);
		Gamemode.sendMessage(player, true, "Adventure");
		return Command.SINGLE_SUCCESS;
	}

	private int spectator(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		Main.log("Command 'gmsp' activated");
		ServerPlayerEntity player = context.getSource().getPlayer();
		player.setGameMode(GameMode.SPECTATOR);
		Gamemode.sendMessage(player, true, "Spectator");
		return Command.SINGLE_SUCCESS;
	}

	private static void sendMessage(ServerPlayerEntity player, boolean success, String gamemode) {
		String message = success ? "command.success.gamemode" : "command.fail.gamemode";
		player.sendSystemMessage(new TranslatableText(message, gamemode), Util.NIL_UUID);
	}

}
