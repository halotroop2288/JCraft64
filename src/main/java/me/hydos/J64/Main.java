package me.hydos.J64;

import me.hydos.J64.emu.util.EmuManager;
import me.hydos.J64.minecraft.registry.BlockRegistry;
import me.hydos.J64.minecraft.registry.ItemRegistry;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class Main implements ModInitializer {

	public static void main(String[] args) {
		EmuManager.setup();
	}

	@Override
	public void onInitialize() {
		System.setProperty("java.awt.headless", "false");
		BlockRegistry.registerAll();
		ItemRegistry.registerAll();
	}
	
	public static Identifier getID(String name)
	{
		return new Identifier("jcraft64", name);
	}
}
