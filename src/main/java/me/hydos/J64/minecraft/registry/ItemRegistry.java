package me.hydos.J64.minecraft.registry;

import java.util.Arrays;

import me.hydos.J64.Main;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemRegistry {
	
	public static final Item TV_ITEM = new BlockItem(BlockRegistry.TV, new Item.Settings().group(ItemGroup.DECORATIONS));
	
	public static Item[] n64_items = new Item[0]; // Call a specific model from this list, or iterate over it, if need be.
	
	public static void registerAll()
	{
		Registry.register(Registry.ITEM, Main.makeID("tv"), TV_ITEM);
	}
	
	public static Item[] registerBlockItem(Identifier id, Block entry) {
		n64_items = Arrays.copyOf(n64_items, n64_items.length + 1);
		n64_items[n64_items.length - 1] = Registry.register(Registry.ITEM, id, new BlockItem(entry, new Item.Settings().group(ItemGroup.DECORATIONS)));
		return n64_items;
	}
}
