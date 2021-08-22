package com.nixinova.morecmds.commands;

import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

import com.nixinova.morecmds.Main;
import com.nixinova.morecmds.Messages;
import com.nixinova.morecmds.Permission;

public class Gamemode {

	public void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			// Survival
			dispatcher.register(literal("gms").executes(this::survival));
			//.then(argument("player", player())).executes(this::survival)
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
		if (!context.getSource().hasPermissionLevel(Permission.OPERATOR)) {
			Messages.permissionMessage("gamemode", player, "Survival");
			return -1;
		}
		player.setGameMode(GameMode.SURVIVAL);
		Messages.genericMessage("success.gamemode", player, "Survival");
		return Command.SINGLE_SUCCESS;
	}

	private int creative(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		Main.log("Command 'gmc' activated");
		ServerPlayerEntity player = context.getSource().getPlayer();
		if (!context.getSource().hasPermissionLevel(Permission.OPERATOR)) {
			Messages.permissionMessage("gamemode", player, "Creative");
			return -1;
		}
		player.setGameMode(GameMode.CREATIVE);
		Messages.genericMessage("success.gamemode", player, "Creative");
		return Command.SINGLE_SUCCESS;
	}

	private int adventure(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		Main.log("Command 'gma' activated");
		ServerPlayerEntity player = context.getSource().getPlayer();
		if (!context.getSource().hasPermissionLevel(Permission.OPERATOR)) {
			Messages.permissionMessage("gamemode", player, "Adventure");
			return -1;
		}
		player.setGameMode(GameMode.ADVENTURE);
		Messages.genericMessage("success.gamemode", player, "Adventure");
		return Command.SINGLE_SUCCESS;
	}

	private int spectator(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		Main.log("Command 'gmsp' activated");
		ServerPlayerEntity player = context.getSource().getPlayer();
		if (!context.getSource().hasPermissionLevel(Permission.OPERATOR)) {
			Messages.permissionMessage("gamemode", player, "Spectator");
			return -1;
		}
		player.setGameMode(GameMode.SPECTATOR);
		Messages.genericMessage("success.gamemode", player, "Spectator");
		return Command.SINGLE_SUCCESS;
	}

}
