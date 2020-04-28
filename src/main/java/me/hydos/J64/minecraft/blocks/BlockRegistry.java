package me.hydos.J64.minecraft.blocks;

import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlockRegistry
{
	public static final Identifier BLACK_N64_ID = new Identifier("jcraft64", "n64_black");
	public static final Block BLACK_N64 = new N64Block(FabricBlockSettings.of(Material.ANVIL).build());
	public static final Item BLACK_N64_ITEM = new BlockItem(BLACK_N64, new Item.Settings().group(ItemGroup.COMBAT));
	public static BlockEntityType<N64BlockEntity> BLACK_N64_BLOCK_ENTITY;
	
	public static void registerAll()
	{
		Registry.register(Registry.BLOCK, BLACK_N64_ID, BLACK_N64);
		Registry.register(Registry.ITEM, BLACK_N64_ID, BLACK_N64_ITEM);
		BLACK_N64_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, BLACK_N64_ID, BlockEntityType.Builder.create(N64BlockEntity::new, BLACK_N64).build(null));
	}
}
