package com.nixinova.morecmds.commands;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.command.argument.EntityArgumentType.player;

import com.nixinova.morecmds.Main;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.world.GameMode;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

public class Gamemode {

	public void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
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
			// Generic
			dispatcher.register(literal("gm")
				.then(literal("survival").executes(this::survival))
				.then(literal("s").executes(this::survival))
				.then(literal("0").executes(this::survival))
				.then(literal("creative").executes(this::creative))
				.then(literal("c").executes(this::creative))
				.then(literal("1").executes(this::creative))
				.then(literal("adventure").executes(this::adventure))
				.then(literal("a").executes(this::adventure))
				.then(literal("2").executes(this::adventure))
				.then(literal("spectator").executes(this::spectator))
				.then(literal("sp").executes(this::spectator))
				.then(literal("3").executes(this::spectator))
			);
		});
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
