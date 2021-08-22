package com.nixinova.morecmds.commands;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.minecraft.command.argument.BlockPosArgumentType.blockPos;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

import com.nixinova.morecmds.Main;
import com.nixinova.morecmds.Messages;
import com.nixinova.morecmds.Permission;

public class Home {

	static final File homesFile = Main.CONFIG_DIR.resolve("homes.txt").toFile();
	static Map<String, float[]> homes = new HashMap<>();

	private enum ArgType {
		SET, REMOVE, GET, GO, LIST
	}

	public void register() {
		Home.setupConfig();
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			dispatcher.register(
				literal("home").then(
					literal("set").then(
						argument("name", string()).then(
							argument("pos", blockPos()).executes(ctx -> home(ctx, ArgType.SET))
						)
					)
				).then(
					literal("get").then(
						argument("name", string()).executes(ctx -> home(ctx, ArgType.GET))
					)
				).then(
					literal("remove").then(
						argument("name", string()).executes(ctx -> home(ctx, ArgType.REMOVE))
					)
				).then(
					literal("go").then(
						argument("name", string()).executes(ctx -> home(ctx, ArgType.GO))
					)
				).then(
					literal("list").executes(ctx -> home(ctx, ArgType.LIST))
				).then(
					argument("name", string()).executes(ctx -> home(ctx, ArgType.GO))
				)
			);
		});
	}

	private int home(CommandContext<ServerCommandSource> context, ArgType type) throws CommandSyntaxException {
		Main.log("Command 'home %s' activated", type.toString().toLowerCase());

		ServerPlayerEntity player = context.getSource().getPlayer();
		if (!context.getSource().hasPermissionLevel(Permission.OPERATOR)) {
			Messages.noPermission("home", player);
			return -1;
		}

		switch (type) {
			case SET: setHome(context); break;
			case REMOVE: removeHome(context); break;
			case GET: getHome(context); break;
			case GO: goHome(context); break;
			case LIST: listHomes(context); break;
		}

		return Command.SINGLE_SUCCESS;
	}

	private void setHome(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		String name = StringArgumentType.getString(context, "name");
		BlockPos pos = BlockPosArgumentType.getBlockPos(context, "pos");
		float[] coords = {pos.getX(), pos.getY(), pos.getZ()};

		homes.put(name, coords);
		writeConfig(name);

		ServerPlayerEntity player = context.getSource().getPlayer();
		Messages.generic("success.home.home.set", player, name, coords[0], coords[1], coords[2]);
	}

	private void removeHome(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		String name = StringArgumentType.getString(context, "name");
		float[] coords = homes.get(name);
		if (coords == null) {
			TranslatableText invalid = new TranslatableText("command.error.home.notFound", name);
			throw new SimpleCommandExceptionType(invalid).create();
		}

		homes.remove(name);
		writeConfig(name);

		ServerPlayerEntity player = context.getSource().getPlayer();
		Messages.generic("success.home.remove", player, name);
	}

	private void getHome(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		String name = StringArgumentType.getString(context, "name");
		float[] coords = homes.get(name);
		if (coords == null) {
			TranslatableText invalid = new TranslatableText("command.error.home.notFound", name);
			throw new SimpleCommandExceptionType(invalid).create();
		}
		ServerPlayerEntity player = context.getSource().getPlayer();
		Messages.generic("success.home.get", player, name, coords[0], coords[1], coords[2]);
	}

	private void goHome(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		String name = StringArgumentType.getString(context, "name");
		float[] coords = homes.get(name);
		if (coords == null) {
			TranslatableText invalid = new TranslatableText("command.error.home.notFound", name);
			throw new SimpleCommandExceptionType(invalid).create();
		}
		ServerPlayerEntity player = context.getSource().getPlayer();
		player.teleport(coords[0], coords[1], coords[2]);
		Messages.generic("success.home.go", player, name);
	}

	private void listHomes(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerPlayerEntity player = context.getSource().getPlayer();
		int total = 0;
		for (Map.Entry<String, float[]> entry : homes.entrySet()) {
			String name = entry.getKey();
			float[] coords = entry.getValue();
			Messages.generic("info.home.list.entry", player, name, coords[0], coords[1], coords[2]);
			total++;
		}
		Messages.generic("info.home.list.total", player, total);
	}

	public static void setupConfig() {
		Main.log("Setting up config file 'homes.txt'");
		try {
			Scanner homesReader = new Scanner(homesFile);
			while (homesReader.hasNextLine()) {
				String line = homesReader.nextLine();
				if (!line.matches(".+:\\d+\\.\\d+/\\d+\\.\\d+/\\d+\\.\\d+")) continue;
				String[] data = line.split(":");
				String name = data[0];
				String[] coordsData = data[1].split("/");
				float[] coords = {0, 0, 0};
				for (int i = 0; i < coordsData.length; i++) {
					coords[i] = Float.parseFloat(coordsData[i]);
				}
				if (name == null || coords.length != 3) continue;
				homes.put(name, coords);
			}
			homesReader.close();
			Main.log("Successfully loaded from config file 'homes.txt'");
		}
		catch (FileNotFoundException err) {
			Main.log("Config file 'homes.txt' does not yet exist");
			System.err.println(err);
		}
	}

	private void writeConfig(String name) {
		try {
			if (homesFile.createNewFile()) {
				Main.log("Created config file 'homes.txt'");
			}
			FileWriter homesFileBuffer = new FileWriter(homesFile);
			for (Map.Entry<String, float[]> entry : homes.entrySet()) {
				String homeName = entry.getKey();
				float[] homeCoords = entry.getValue();
				homesFileBuffer.write(String.format("%s:%f/%f/%f\n", homeName, homeCoords[0], homeCoords[1], homeCoords[2]));
			}
			homesFileBuffer.close();
			Main.log(String.format("Edited data for home '%s' in config file 'homes.txt'", name));
		}
		catch (IOException err) {
			Main.log("Error while creating config file 'homes.txt'");
			System.err.println(err);
		}

	}

}
