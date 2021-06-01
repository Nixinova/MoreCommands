package com.nixinova.morecmds.commands;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static com.mojang.brigadier.arguments.FloatArgumentType.floatArg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import com.nixinova.morecmds.Main;
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
							argument("x", floatArg()).then(
								argument("y", floatArg()).then(
									argument("z", floatArg()).executes(this::setHome)
								)
							)
						)
					)
				).then(
					literal("get").then(
						argument("name", string()).executes(this::getHome)
					)
				).then(
					literal("go").then(
						argument("name", string()).executes(this::goHome)
					)
				).then(
					argument("name", string()).executes(this::goHome)
				)
			);
		});
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
		}
	}

	//private void setConfig(CommandContext<ServerCommandSource> context) {
	//	context.getSource().getWorld();
	//}

	private int setHome(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		Main.log("Command 'home set' activated");
		String name = StringArgumentType.getString(context, "name");
		float x = FloatArgumentType.getFloat(context, "x");
		float y = FloatArgumentType.getFloat(context, "y");
		float z = FloatArgumentType.getFloat(context, "z");
		float[] coords = {x, y, z};

		ServerPlayerEntity player = context.getSource().getPlayer();
		if (!context.getSource().hasPermissionLevel(Permission.OPERATOR)) {
			player.sendSystemMessage(new TranslatableText("command.home.error.permission"), Util.NIL_UUID);
		}

		homes.put(name, coords);
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
			Main.log(String.format("Wrote home '%s' to config file 'homes.txt'", name));
		} catch (IOException err) {
			Main.log("Error while creating config file 'homes.txt'");
			System.err.println(err);
		}

		TranslatableText output = new TranslatableText("command.success.home.set", name, coords[0], coords[1], coords[2]);
		player.sendSystemMessage(output, Util.NIL_UUID);
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
			player.sendSystemMessage(new TranslatableText("command.home.error.permission"), Util.NIL_UUID);
		}

		TranslatableText output = new TranslatableText("command.success.home.get", name, coords[0], coords[1], coords[2]);
		player.sendSystemMessage(output, Util.NIL_UUID);
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
			player.sendSystemMessage(new TranslatableText("command.home.error.permission"), Util.NIL_UUID);
		}
		player.sendSystemMessage(new TranslatableText("command.success.home.go", name), Util.NIL_UUID);
		player.teleport(coords[0], coords[1], coords[2]);
		return Command.SINGLE_SUCCESS;
	}

}
