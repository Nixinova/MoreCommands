package com.nixinova.morecmds.commands;

import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.block.Blocks;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import com.nixinova.morecmds.Main;
import com.nixinova.morecmds.Messages;
import com.nixinova.morecmds.Permission;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Structure {

	public void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			dispatcher.register(
				literal("structure")
				.then(
					argument("structure_keywords", string()).then(
						argument("direction", word())
						.executes(ctx -> createStructure(ctx))
					).executes(ctx -> createStructure(ctx))
				)
			);
		});
	}

	private int createStructure(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		Main.log("Command 'structure' activated");
		ServerPlayerEntity player = context.getSource().getPlayer();
		World world = player.getEntityWorld();
		BlockPos pos = player.getBlockPos();
		CommandManager manager = player.getServer().getCommandManager();
		ServerCommandSource source = player.getCommandSource();

		// Get arguments
		String query = getString(context, "structure_keywords");
		String direction;
		try {
			direction = getString(context, "direction");
		} catch (IllegalArgumentException err) {
			direction = "unspecified";
		}

		// Player must have Trusted permission to use
		if (!context.getSource().hasPermissionLevel(Permission.TRUSTED)) {
			Messages.noPermission("structure", player);
			return -1;
		}

		// Get list of applicable structures
		List<String> structures = getApplicableStructures(query);
		// Exit if no structure matches query
		if (structures.size() == 0) {
			Messages.generic("error.structure.notFound", player, query);
			return -1;
		}
		// Select random structure
		int randIndex = (int) Math.floor(Math.random() * structures.size());
		String structure = structures.get(randIndex);
		Messages.generic("info.structure.count", player, randIndex, structures.size(), query);

		// Select direction
		String rotation = switch (direction) {
			case "southeast", "south", "0", "360" -> "NONE";
			case "southwest", "west", "90" -> "CLOCKWISE_90";
			case "northwest", "north", "180", "-180" -> "CLOCKWISE_180";
			case "northeast", "east", "270", "-90" -> "COUNTERCLOCKWISE_90";
			case "unspecified" -> "";
			default -> "invalid";
		};
		if (rotation == "invalid") {
			Messages.generic("error.structure.direction.invalid", player, direction);
			return -1;
		}

		// Place selected structure
		String blockData = String.format("{mode:'LOAD',name:'%s',rotation:'%s'}", structure, rotation);
		manager.execute(source, "setblock ~ ~ ~ minecraft:structure_block" + blockData);
		world.setBlockState(pos.add(0, 1, 0), Blocks.REDSTONE_BLOCK.getDefaultState());
		world.setBlockState(pos.add(0, 1, 0), Blocks.AIR.getDefaultState());
		world.setBlockState(pos.add(0, 0, 0), Blocks.AIR.getDefaultState());

		// Return
		Messages.generic("success.structure", player, structure);
		return Command.SINGLE_SUCCESS;
	}

	private List<String> getApplicableStructures(String search) {
		String[] structures = readStructuresList().split("\n");
		String searchMatch = search.replaceAll("[^a-z0-9]", ".*");
		List<String> applicable = new ArrayList<>();
		for (String structure : structures) {
			String cleanStructure = structure.replaceAll("[^a-z0-9]", "");
			boolean match = Pattern.matches(".*" + searchMatch + ".*", cleanStructure);
			if (!match) continue;
			applicable.add(structure);
		}
		return applicable;
	}

	private String readStructuresList() {
		InputStream in = getClass().getResourceAsStream("/lists/structures.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder result = new StringBuilder();
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				result.append(line).append("\n");
			}
		} catch (IOException err) {
			Main.log("Failed to read structures file");
		}
		return result.toString();
	}

}
