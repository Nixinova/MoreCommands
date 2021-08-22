package com.nixinova.morecmds.commands;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.minecraft.command.argument.BlockStateArgumentType.blockState;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.BlockStateArgument;

import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

import com.nixinova.morecmds.Main;
import com.nixinova.morecmds.Messages;
import com.nixinova.morecmds.Permission;

public class Shape {

	public void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			dispatcher.register(
				literal("shape")
				.then(
					literal("box").then(
						argument("x", integer()).then(
							argument("y", integer()).then(
								argument("z", integer()).then(
									argument("block", blockState()).executes(this::createBox)
								)
							)
						)
					)
				).then(
					literal("cube").then(
						argument("size", integer()).then(
							argument("block", blockState()).executes(this::createCube)
						)
					)
				).then(
					literal("pyramid").then(
						argument("size", integer()).then(
							argument("block", blockState()).executes(this::createPyramid)
						)
					)
				).then(
					literal("sphere").then(
						argument("radius", integer()).then(
							argument("block", blockState()).executes(this::createSphere)
						)
					)
				)
			);
		});
	}

	private int createBox(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		Main.log("Command 'shape box' activated");

		ServerPlayerEntity player = context.getSource().getPlayer();
		if (!context.getSource().hasPermissionLevel(Permission.OPERATOR)) {
			Messages.permissionMessage("shape", player, "box");
			return -1;
		}

		int inputX = IntegerArgumentType.getInteger(context, "x");
		int inputY = IntegerArgumentType.getInteger(context, "y");
		int inputZ = IntegerArgumentType.getInteger(context, "z");
		BlockStateArgument block = BlockStateArgumentType.getBlockState(context, "block");

		World world = player.getEntityWorld();
		BlockPos pos = player.getBlockPos();
		for (int x = 0; x < inputX; x++) for (int y = 0; y < inputY; y++) for (int z = 0; z < inputZ; z++) {
			if (x != 0 && y != 0 && z != 0 && x != inputX-1 && y != inputY-1 && z != inputZ-1) continue;
			world.setBlockState(pos.add(-x, -y, -z), block.getBlockState());
		}
		player.setPos(pos.getX(), pos.getY() + 1, pos.getZ());

		Messages.genericMessage("success.shape", player, "box");
		return Command.SINGLE_SUCCESS;
	}

	private int createCube(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		Main.log("Command 'shape cube' activated");

		ServerPlayerEntity player = context.getSource().getPlayer();
		if (!context.getSource().hasPermissionLevel(Permission.OPERATOR)) {
			Messages.permissionMessage("shape", player, "cube");
			return -1;
		}

		int size = IntegerArgumentType.getInteger(context, "size");
		BlockStateArgument block = BlockStateArgumentType.getBlockState(context, "block");

		World world = player.getEntityWorld();
		BlockPos pos = player.getBlockPos();
		for (int x = 0; x < size; x++) for (int y = 0; y < size; y++) for (int z = 0; z < size; z++) {
			world.setBlockState(pos.add(-x, -y, -z), block.getBlockState());
		}
		player.setPos(pos.getX(), pos.getY() + 1, pos.getZ());

		Messages.genericMessage("success.shape", player, "cube");
		return Command.SINGLE_SUCCESS;
	}

	private int createPyramid(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		Main.log("Command 'shape cube' pyramid");

		ServerPlayerEntity player = context.getSource().getPlayer();
		if (!context.getSource().hasPermissionLevel(Permission.OPERATOR)) {
			Messages.permissionMessage("shape", player, "pyramid");
			return -1;
		}

		int size = IntegerArgumentType.getInteger(context, "size");
		BlockStateArgument block = BlockStateArgumentType.getBlockState(context, "block");

		World world = player.getEntityWorld();
		BlockPos pos = player.getBlockPos();
		for (int y = 0; y < size; y++) for (int x = -y; x <= y; x++)  for (int z = -y; z <= y; z++) {
			world.setBlockState(pos.add(-x, -y, -z), block.getBlockState());
		}
		player.setPos(pos.getX(), pos.getY() + 1, pos.getZ());

		Messages.genericMessage("success.shape", player, "pyramid");
		return Command.SINGLE_SUCCESS;
	}

	private int createSphere(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		Main.log("Command 'shape sphere' activated");

		ServerPlayerEntity player = context.getSource().getPlayer();
		if (!context.getSource().hasPermissionLevel(Permission.OPERATOR)) {
			Messages.permissionMessage("shape", player, "sphere");
			return -1;
		}

		int r = IntegerArgumentType.getInteger(context, "radius");
		BlockStateArgument block = BlockStateArgumentType.getBlockState(context, "block");

		World world = player.getEntityWorld();
		BlockPos pos = player.getBlockPos();
		for (int x = 0; x < r*2; x++) for (int y = 0; y < r*2; y++) for (int z = 0; z < r*2; z++) {
			if (x*x + y*y + z*z > r*r) continue;
			// Outer loop only does one quarter sphere
			// Inner loop is needed to do each permutation of the negative of each coordinate for the other quarters
			for (int i = -1; i <= 1; i += 2) for (int j = -1; j <= 1; j += 2) for (int k = -1; k <= 1; k += 2) {
				world.setBlockState(pos.add(x*i, y*j, z*k), block.getBlockState());
			}
		}
		player.setPos(pos.getX(), pos.getY() + r + 1, pos.getZ());

		Messages.genericMessage("success.shape", player, "sphere");
		return Command.SINGLE_SUCCESS;
	}

}
