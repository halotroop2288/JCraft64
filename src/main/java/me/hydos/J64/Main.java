package me.hydos.J64;

import me.hydos.J64.util.EmuManager;
import net.fabricmc.api.ModInitializer;

public class Main implements ModInitializer {

	@Override
	public void onInitialize() {
		EmuManager.setup();
	}
}
