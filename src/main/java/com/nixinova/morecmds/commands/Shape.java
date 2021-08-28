package com.nixinova.morecmds.commands;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static net.minecraft.command.argument.BlockStateArgumentType.blockState;
import static net.minecraft.command.argument.BlockStateArgumentType.getBlockState;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import java.util.function.Predicate;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
		public boolean takeAwayItems;
		public int placedBlockCount;
		public int blocksInInventory;

		public ShapeData(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
			this.context = context;
			this.player = context.getSource().getPlayer();
			this.world = player.getEntityWorld();
			this.pos = player.getBlockPos();
			this.takeAwayItems = false;
			this.placedBlockCount = 0;
			this.blocksInInventory = 0;
		}
		public void shouldTakeAwayItems() {
			this.takeAwayItems = true;
		}
		public void setAvailableItems(int amount) {
			this.blocksInInventory = amount;
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
									argument("block", blockState())
									.executes(ctx -> createShape(ctx, Preset.BOX))
								)
							)
						)
					)
				).then(
					literal("cube").then(
						argument("size", integer()).then(
							argument("block", blockState())
							.executes(ctx -> createShape(ctx, Preset.CUBE))
						)
					)
				).then(
					literal("pyramid").then(
						argument("size", integer()).then(
							argument("block", blockState())
							.executes(ctx -> createShape(ctx, Preset.PYRAMID))
						)
					)
				).then(
					literal("sphere").then(
						argument("radius", integer()).then(
							argument("block", blockState())
							.executes(ctx -> createShape(ctx, Preset.SPHERE))
						)
					)
				)
			);
		});
	}

	private int createShape(CommandContext<ServerCommandSource> context, Preset shape) throws CommandSyntaxException {
		String shapeName = shape.toString().toLowerCase();
		Main.log("Command 'shape %s' activated", shapeName);
		ShapeData data = new ShapeData(context);
		ServerPlayerEntity player = context.getSource().getPlayer();
		BlockStateArgument block = getBlockState(context, "block");
		Item itemForm = Item.fromBlock(block.getBlockState().getBlock());

		// Player must have Member permission to use any part of the command
		if (!context.getSource().hasPermissionLevel(Permission.MEMBER)) {
			Messages.noPermission("shape", player, shapeName);
			return -1;
		}

		// Player must then have Trusted permission to not have blocks removed for each use
		if (!context.getSource().hasPermissionLevel(Permission.TRUSTED)) {
			data.shouldTakeAwayItems();
			int availableItems = player.inventory.count(itemForm);
			data.setAvailableItems(availableItems);
		}

		// Create shape
		switch (shape) {
			case BOX: createBox(data); break;
			case CUBE: createCube(data); break;
			case PYRAMID: createPyramid(data); break;
			case SPHERE: createSphere(data); break;
		}

		// Take away items if applicable
		if (data.takeAwayItems) {
			if (data.placedBlockCount > 0) Messages.generic("info.shape.removingBlocks", player, data.placedBlockCount);
			Predicate<ItemStack> check = (stack) -> ItemStack.areItemsEqual(stack, itemForm.getDefaultStack());
			player.inventory.remove(check, data.placedBlockCount, null /* expect error */);
			Inventories.remove(player.inventory, check, data.placedBlockCount, false);
		}

		// Success
		Messages.generic("success.shape", player, shapeName);
		return Command.SINGLE_SUCCESS;
	}

	private void createBox(ShapeData data) {
		int inputX = getInteger(data.context, "x");
		int inputY = getInteger(data.context, "y");
		int inputZ = getInteger(data.context, "z");
		BlockStateArgument block = getBlockState(data.context, "block");
		for (int x = 0; x < inputX; x++) for (int y = 0; y < inputY; y++) for (int z = 0; z < inputZ; z++) {
			if (!blockCountValid(data)) return;
			data.world.setBlockState(data.pos.add(-x, -y, -z), block.getBlockState());
		}
	}

	private void createCube(ShapeData data) {
		int size = getInteger(data.context, "size");
		BlockStateArgument block = getBlockState(data.context, "block");
		for (int x = 0; x < size; x++) for (int y = 0; y < size; y++) for (int z = 0; z < size; z++) {
			if (!blockCountValid(data)) return;
			data.world.setBlockState(data.pos.add(-x, -y, -z), block.getBlockState());
		}
	}

	private void createPyramid(ShapeData data) {
		int size = getInteger(data.context, "size");
		BlockStateArgument block = getBlockState(data.context, "block");
		for (int y = 0; y < size; y++) for (int x = -y; x <= y; x++)  for (int z = -y; z <= y; z++) {
			if (!blockCountValid(data)) return;
			data.world.setBlockState(data.pos.add(-x, -y, -z), block.getBlockState());
		}
	}

	private void createSphere(ShapeData data) {
		int r = getInteger(data.context, "radius");
		BlockStateArgument block = getBlockState(data.context, "block");
		for (int x = 0; x < r*2; x++) for (int y = 0; y < r*2; y++) for (int z = 0; z < r*2; z++) {
			if (x*x + y*y + z*z > r*r) continue;
			// Outer loop only does one quarter sphere
			// Inner loop is needed to do each permutation of the negative of each coordinate for the other quarters
			for (int i = -1; i <= 1; i += 2) for (int j = -1; j <= 1; j += 2) for (int k = -1; k <= 1; k += 2) {
				if (!blockCountValid(data)) return;
				data.world.setBlockState(data.pos.add(x*i, y*j, z*k), block.getBlockState());
			}
		}
		data.player.setPos(data.pos.getX(), data.pos.getY() + r, data.pos.getZ());
	}

	private static boolean blockCountValid(ShapeData data) {
		if (!data.takeAwayItems) return true;
		if (data.placedBlockCount < data.blocksInInventory) {
			data.placedBlockCount++;
			return true;
		}
		else {
			Messages.generic("error.shape.noBlocks", data.player);
			return false;
		}
	}

}
