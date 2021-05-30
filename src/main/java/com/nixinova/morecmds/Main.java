package com.nixinova.morecmds;

import com.nixinova.morecmds.commands.Gamemode;

import net.fabricmc.api.ModInitializer;

public class Main implements ModInitializer {

	@Override
	public void onInitialize() {
		new Gamemode().register();
	}

	public static void debug(String msg) {
		System.out.println("MoreCMDs> " + msg);
	}

}
