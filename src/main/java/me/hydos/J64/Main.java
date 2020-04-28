package me.hydos.J64;

import me.hydos.J64.emu.util.EmuManager;
import me.hydos.J64.minecraft.blocks.BlockRegistry;
import net.fabricmc.api.ModInitializer;

public class Main implements ModInitializer {


	public static void main(String[] args) {
		EmuManager.setup();
	}

	@Override
	public void onInitialize() {
		System.setProperty("java.awt.headless", "false");
		BlockRegistry.registerAll();
	}
}
