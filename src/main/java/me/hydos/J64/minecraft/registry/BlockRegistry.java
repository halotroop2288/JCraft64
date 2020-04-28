package me.hydos.J64.minecraft.registry;

import me.hydos.J64.Main;
import me.hydos.J64.minecraft.blocks.N64Block;
import me.hydos.J64.minecraft.blocks.N64BlockEntity;
import me.hydos.J64.minecraft.blocks.TVBlock;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

public class BlockRegistry
{
	public static final Block BLACK_N64 = new N64Block(FabricBlockSettings.of(Material.ANVIL).nonOpaque().build());
	public static BlockEntityType<N64BlockEntity> BLACK_N64_BLOCK_ENTITY;
	public static final Block TV = new TVBlock(FabricBlockSettings.copy(Blocks.IRON_BLOCK).nonOpaque().build());
	
	public static void registerAll() {
		Registry.register(Registry.BLOCK, Main.getID("n64_black"), BLACK_N64);
		Registry.register(Registry.BLOCK, Main.getID("tv"), TV);
		BLACK_N64_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, Main.getID("n64_black"), BlockEntityType.Builder.create(N64BlockEntity::new, BLACK_N64).build(null));
	}
}
