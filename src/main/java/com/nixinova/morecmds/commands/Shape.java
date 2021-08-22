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

	private enum Preset {
		BOX, CUBE, PYRAMID, SPHERE
	}

	private class ShapeData {
		public CommandContext<ServerCommandSource> context;
		public ServerPlayerEntity player;
		public World world;
		public BlockPos pos;

		public ShapeData(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
			this.context = context;
			this.player = context.getSource().getPlayer();
			this.world = player.getEntityWorld();
			this.pos = player.getBlockPos();
		}
	}

	public void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			dispatcher.register(
				literal("shape")
				.then(
					literal("box").then(
						argument("x", integer()).then(
							argument("y", integer()).then(
								argument("z", integer()).then(
									argument("block", blockState()).executes(ctx -> createShape(ctx, Preset.BOX))
								)
							)
						)
					)
				).then(
					literal("cube").then(
						argument("size", integer()).then(
							argument("block", blockState()).executes(ctx -> createShape(ctx, Preset.CUBE))
						)
					)
				).then(
					literal("pyramid").then(
						argument("size", integer()).then(
							argument("block", blockState()).executes(ctx -> createShape(ctx, Preset.PYRAMID))
						)
					)
				).then(
					literal("sphere").then(
						argument("radius", integer()).then(
							argument("block", blockState()).executes(ctx -> createShape(ctx, Preset.SPHERE))
						)
					)
				)
			);
		});
	}

	private int createShape(CommandContext<ServerCommandSource> context, Preset shape) throws CommandSyntaxException {
		String shapeName = shape.toString().toLowerCase();
		Main.log("Command 'shape %s' activated", shapeName);

		ServerPlayerEntity player = context.getSource().getPlayer();
		if (!context.getSource().hasPermissionLevel(Permission.OPERATOR)) {
			Messages.noPermission("shape", player, shapeName);
			return -1;
		}

		ShapeData data = new ShapeData(context);
		switch (shape) {
			case BOX: createBox(data); break;
			case CUBE: createCube(data); break;
			case PYRAMID: createPyramid(data); break;
			case SPHERE: createSphere(data); break;
		}

		Messages.generic("success.shape", player, shapeName);
		return Command.SINGLE_SUCCESS;
	}

	private void createBox(ShapeData data) throws CommandSyntaxException {
		int inputX = IntegerArgumentType.getInteger(data.context, "x");
		int inputY = IntegerArgumentType.getInteger(data.context, "y");
		int inputZ = IntegerArgumentType.getInteger(data.context, "z");
		BlockStateArgument block = BlockStateArgumentType.getBlockState(data.context, "block");
		for (int x = 0; x < inputX; x++) for (int y = 0; y < inputY; y++) for (int z = 0; z < inputZ; z++) {
			if (x != 0 && y != 0 && z != 0 && x != inputX-1 && y != inputY-1 && z != inputZ-1) continue;
			data.world.setBlockState(data.pos.add(-x, -y, -z), block.getBlockState());
		}
	}

	private void createCube(ShapeData data) throws CommandSyntaxException {
		int size = IntegerArgumentType.getInteger(data.context, "size");
		BlockStateArgument block = BlockStateArgumentType.getBlockState(data.context, "block");
		for (int x = 0; x < size; x++) for (int y = 0; y < size; y++) for (int z = 0; z < size; z++) {
			data.world.setBlockState(data.pos.add(-x, -y, -z), block.getBlockState());
		}
	}

	private void createPyramid(ShapeData data) throws CommandSyntaxException {
		int size = IntegerArgumentType.getInteger(data.context, "size");
		BlockStateArgument block = BlockStateArgumentType.getBlockState(data.context, "block");
		for (int y = 0; y < size; y++) for (int x = -y; x <= y; x++)  for (int z = -y; z <= y; z++) {
			data.world.setBlockState(data.pos.add(-x, -y, -z), block.getBlockState());
		}
	}

	private void createSphere(ShapeData data) throws CommandSyntaxException {
		int r = IntegerArgumentType.getInteger(data.context, "radius");
		BlockStateArgument block = BlockStateArgumentType.getBlockState(data.context, "block");
		for (int x = 0; x < r*2; x++) for (int y = 0; y < r*2; y++) for (int z = 0; z < r*2; z++) {
			if (x*x + y*y + z*z > r*r) continue;
			// Outer loop only does one quarter sphere
			// Inner loop is needed to do each permutation of the negative of each coordinate for the other quarters
			for (int i = -1; i <= 1; i += 2) for (int j = -1; j <= 1; j += 2) for (int k = -1; k <= 1; k += 2) {
				data.world.setBlockState(data.pos.add(x*i, y*j, z*k), block.getBlockState());
			}
		}
		data.player.setPos(data.pos.getX(), data.pos.getY() + r, data.pos.getZ());
	}

}
