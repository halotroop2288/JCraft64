package me.hydos.J64;

import me.hydos.J64.emu.util.EmuManager;
import me.hydos.J64.minecraft.blocks.N64Block;
import me.hydos.J64.minecraft.blocks.N64BlockEntity;
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
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Main implements ModInitializer {

	public static final Identifier N64_ID = new Identifier("jcraft64", "black_n64");
	public static final Block N64 = new N64Block(FabricBlockSettings.of(Material.ANVIL).build());
	public static final Item N64_ITEM = new BlockItem(N64, new Item.Settings().group(ItemGroup.COMBAT));
	public static BlockEntityType<N64BlockEntity> n64BlockEntity;

	public static void main(String[] args) {
		EmuManager.setup();
	}

	@Override
	public void onInitialize() {
		System.setProperty("java.awt.headless", "false");
		Registry.register(Registry.BLOCK, N64_ID, N64);
		Registry.register(Registry.ITEM, N64_ID, N64_ITEM);
		n64BlockEntity = Registry.register(Registry.BLOCK_ENTITY_TYPE, N64_ID, BlockEntityType.Builder.create(N64BlockEntity::new, N64).build(null));
	}
}
