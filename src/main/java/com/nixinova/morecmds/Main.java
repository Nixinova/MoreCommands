package com.nixinova.morecmds;

import java.io.File;
import java.nio.file.Path;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import com.nixinova.morecmds.commands.Gamemode;
import com.nixinova.morecmds.commands.Home;

public class Main implements ModInitializer {

	public static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve("morecmds/");

	@Override
	public void onInitialize() {
		// Initialise config directory
		File configDir = CONFIG_DIR.toFile();
		if (!configDir.exists()) configDir.mkdirs();
		// Register commands
		new Gamemode().register();
		new Home().register();
	}

	public static void log(String msg) {
		System.out.println("[MoreCMDs] " + msg);
	}
	public static void debug(String msg) {
		Main.log("[DEBUG] " + msg);
	}

}
