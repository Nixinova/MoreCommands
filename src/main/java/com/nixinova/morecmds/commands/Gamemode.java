package com.nixinova.morecmds.commands;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameMode;

import com.nixinova.morecmds.Main;
import com.nixinova.morecmds.Messages;
import com.nixinova.morecmds.Permission;

public class Gamemode {

	public void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			// Survival
			dispatcher.register(literal("gms").executes(ctx -> gamemode(ctx, "s")));
			//.then(argument("player", player())).executes(ctx -> gamemode(ctx, "s"))
			dispatcher.register(literal("gm0").executes(ctx -> gamemode(ctx, "s")));
			// Creative
			dispatcher.register(literal("gmc").executes(ctx -> gamemode(ctx, "c")));
			dispatcher.register(literal("gm1").executes(ctx -> gamemode(ctx, "c")));
			// Adventure
			dispatcher.register(literal("gma").executes(ctx -> gamemode(ctx, "a")));
			dispatcher.register(literal("gm2").executes(ctx -> gamemode(ctx, "a")));
			// Spectator
			dispatcher.register(literal("gmsp").executes(ctx -> gamemode(ctx, "sp")));
			dispatcher.register(literal("gm3").executes(ctx -> gamemode(ctx, "sp")));
			// Generic
			dispatcher.register(literal("gm").then(
				argument("mode", StringArgumentType.word())
				.executes(ctx -> gamemode(ctx, StringArgumentType.getString(ctx, "mode")))
			));
		});
	}

	private int gamemode(CommandContext<ServerCommandSource> context, String type) throws CommandSyntaxException {
		Main.log("Command 'gm %s' activated", type);

		String gamemode = switch (type) {
			case "0", "s", "survival" -> "Survival";
			case "1", "c", "creative" -> "Creative";
			case "2", "a", "adventure" -> "Adventure";
			case "3", "sp", "spectator" -> "Spectator";
			default -> {
				TranslatableText invalid = new TranslatableText("command.error.gamemode.invalid", type);
				throw new SimpleCommandExceptionType(invalid).create();
			}
		};

		ServerPlayerEntity player = context.getSource().getPlayer();
		if (!context.getSource().hasPermissionLevel(Permission.TRUSTED)) {
			Messages.noPermission("gamemode", player, gamemode);
			return -1;
		}

		switch (gamemode) {
			case "Survival" -> player.changeGameMode(GameMode.SURVIVAL);
			case "Creative" -> player.changeGameMode(GameMode.CREATIVE);
			case "Adventure" -> player.changeGameMode(GameMode.ADVENTURE);
			case "Spectator" -> player.changeGameMode(GameMode.SPECTATOR);
		}

		Messages.generic("success.gamemode", player, gamemode);
		return Command.SINGLE_SUCCESS;
	}

}
