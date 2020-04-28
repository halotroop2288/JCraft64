package me.hydos.J64;

import me.hydos.J64.emu.util.EmuManager;
import me.hydos.J64.minecraft.registry.BlockRegistry;
import me.hydos.J64.minecraft.registry.ItemRegistry;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

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
	
	public static Identifier makeID(String name)
	{
		return new Identifier("jcraft64", name);
	}
}
