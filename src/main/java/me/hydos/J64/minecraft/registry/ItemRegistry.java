package me.hydos.J64.minecraft.registry;

import me.hydos.J64.Main;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.registry.Registry;

public class ItemRegistry {
	
	public static final Item BLACK_N64_ITEM = new BlockItem(BlockRegistry.BLACK_N64, new Item.Settings().group(ItemGroup.DECORATIONS));
	public static final Item TV_ITEM = new BlockItem(BlockRegistry.TV, new Item.Settings().group(ItemGroup.DECORATIONS));
	
	public static void registerAll()
	{
		Registry.register(Registry.ITEM, Main.getID("n64_black"), BLACK_N64_ITEM);
		Registry.register(Registry.ITEM, Main.getID("tv"), TV_ITEM);
	}
}
