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
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;

import com.nixinova.morecmds.Main;
import com.nixinova.morecmds.Messages;
import com.nixinova.morecmds.Permission;

public class Home {

	static final File homesFile = Main.CONFIG_DIR.resolve("homes.txt").toFile();
	static Map<String, float[]> homes = new HashMap<>();

	public void register() {
		Home.setupConfig();
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			dispatcher.register(
				literal("home").then(
					literal("set").then(
						argument("name", string()).then(
							argument("pos", blockPos()).executes(this::setHome)
						)
					)
				).then(
					literal("get").then(
						argument("name", string()).executes(this::getHome)
					)
				).then(
					literal("remove").then(
						argument("name", string()).executes(this::removeHome)
					)
				).then(
					literal("go").then(
						argument("name", string()).executes(this::goHome)
					)
				).then(
					literal("list").executes(this::listHomes)
				).then(
					argument("name", string()).executes(this::goHome)
				)
			);
		});
	}

	private int setHome(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		Main.log("Command 'home set' activated");
		String name = StringArgumentType.getString(context, "name");
		BlockPos pos = BlockPosArgumentType.getBlockPos(context, "pos");
		float[] coords = {pos.getX(), pos.getY(), pos.getZ()};

		ServerPlayerEntity player = context.getSource().getPlayer();
		if (!context.getSource().hasPermissionLevel(Permission.OPERATOR)) {
			Messages.permissionMessage("home", player);
			return -1;
		}

		homes.put(name, coords);
		writeConfigFile(name);

		TranslatableText output = new TranslatableText("command.success.home.set", name, coords[0], coords[1], coords[2]);
		player.sendSystemMessage(output, Util.NIL_UUID);
		return Command.SINGLE_SUCCESS;
	}

	private int removeHome(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		Main.log("Command 'home remove' activated");
		String name = StringArgumentType.getString(context, "name");
		float[] coords = homes.get(name);
		if (coords == null) {
			TranslatableText invalid = new TranslatableText("command.error.home.notFound", name);
			throw new SimpleCommandExceptionType(invalid).create();
		}

		ServerPlayerEntity player = context.getSource().getPlayer();
		if (!context.getSource().hasPermissionLevel(Permission.OPERATOR)) {
			Messages.permissionMessage("home", player);
			return -1;
		}

		homes.remove(name);
		writeConfigFile(name);

		Messages.genericMessage("success.home.remove", player, name);
		return Command.SINGLE_SUCCESS;
	}

	private int getHome(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		Main.log("Command 'home get' activated");
		String name = StringArgumentType.getString(context, "name");
		float[] coords = homes.get(name);
		if (coords == null) {
			TranslatableText invalid = new TranslatableText("command.error.home.notFound", name);
			throw new SimpleCommandExceptionType(invalid).create();
		}

		ServerPlayerEntity player = context.getSource().getPlayer();
		if (!context.getSource().hasPermissionLevel(Permission.OPERATOR)) {
			Messages.permissionMessage("home", player);
			return -1;
		}

		Messages.genericMessage("success.home.get", player, name, coords[0], coords[1], coords[2]);
		return Command.SINGLE_SUCCESS;
	}

	private int goHome(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		Main.log("Command 'home go' activated");
		String name = StringArgumentType.getString(context, "name");
		float[] coords = homes.get(name);
		if (coords == null) {
			TranslatableText invalid = new TranslatableText("command.error.home.notFound", name);
			throw new SimpleCommandExceptionType(invalid).create();
		}

		ServerPlayerEntity player = context.getSource().getPlayer();
		if (!context.getSource().hasPermissionLevel(Permission.OPERATOR)) {
			Messages.permissionMessage("home", player);
			return -1;
		}

		player.teleport(coords[0], coords[1], coords[2]);
		Messages.genericMessage("success.home.go", player, name);
		return Command.SINGLE_SUCCESS;
	}

	private int listHomes(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		Main.log("Command 'home list' activated");

		ServerPlayerEntity player = context.getSource().getPlayer();
		if (!context.getSource().hasPermissionLevel(Permission.OPERATOR)) {
			Messages.permissionMessage("home", player);
			return -1;
		}

		int total = 0;
		for (Map.Entry<String, float[]> entry : homes.entrySet()) {
			String name = entry.getKey();
			float[] coords = entry.getValue();
			player.sendSystemMessage(new TranslatableText("command.info.home.list.entry", name, coords[0], coords[1], coords[2]), Util.NIL_UUID);
			total++;
		}
		player.sendSystemMessage(new TranslatableText("command.info.home.list.total", total), Util.NIL_UUID);
		return Command.SINGLE_SUCCESS;
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

	private void writeConfigFile(String name) {
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
