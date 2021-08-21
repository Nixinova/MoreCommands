package com.nixinova.morecmds;

import net.minecraft.text.TranslatableText;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Util;

public class Messages {

	public static void genericMessage(String type, ServerPlayerEntity player, boolean success, Object... parts) {
		String message = success ? "command.success." + type : "command.fail." + type;
		player.sendSystemMessage(new TranslatableText(message, parts), Util.NIL_UUID);
	}

	public static void permissionMessage(String type, ServerPlayerEntity player, Object... parts) {
		genericMessage(type, player, false, parts);
		TranslatableText failMessage = new TranslatableText("command." + type + ".error.permission");
		player.sendSystemMessage(failMessage, Util.NIL_UUID);
	}

}
