package com.nixinova.morecmds;

import net.minecraft.text.TranslatableText;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Util;

public class Messages {

	public static void genericMessage(String text, ServerPlayerEntity player, Object... parts) {
		player.sendSystemMessage(new TranslatableText("command." + text, parts), Util.NIL_UUID);
	}

	public static void permissionMessage(String type, ServerPlayerEntity player, Object... parts) {
		genericMessage("error." + type, player, parts);
		TranslatableText failMessage = new TranslatableText("command." + type + ".error.permission");
		player.sendSystemMessage(failMessage, Util.NIL_UUID);
	}

}
