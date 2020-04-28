package me.hydos.J64.minecraft.registry;

import me.hydos.J64.Main;
import me.hydos.J64.minecraft.block.N64Block;
import me.hydos.J64.minecraft.block.TVBlock;
import me.hydos.J64.minecraft.block.entity.N64BlockEntity;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlockRegistry {
	public static BlockEntityType<N64BlockEntity> N64_BE_TYPE;
	public static final Block TV = new TVBlock(FabricBlockSettings.copy(Blocks.IRON_BLOCK).build());
	
	private static String[] n64_colours = // https://consolevariations.com/blog/every-nintendo-64-console-variation-ever-complete-color-list
		{
//			"jungle_green", "ice_blue", "grape_purple", "fire_orange", "smoke_black", "watermelon_red", "white_blue", "white_red",
//			"pikachu_dark_blue", "pikachu_light_blue", "pikachu_orange", "pokemon_stadium",
//			"gold", "charcoal_orange",
			"charcoal"
		};
	public static Block[] n64_blocks = new Block[n64_colours.length]; // Call a specific model from this list, or iterate over them, if need be.

	public static void registerAll() {
	    for (String colour : n64_colours)
	    {
	    	Identifier id = Main.makeID("n64_" + colour);
			Block block = Registry.register(Registry.BLOCK, id, new N64Block(FabricBlockSettings.of(Material.CLAY).build()));
			n64_blocks[n64_blocks.length - 1] = block;
			ItemRegistry.registerBlockItem(id, block); // If you can find a good way to move this to ItemRegistry, BE MY GUEST!
	    }
	    Registry.register(Registry.BLOCK, Main.makeID("tv"), TV);
		N64_BE_TYPE = Registry.register(Registry.BLOCK_ENTITY_TYPE, Main.makeID("n64"), BlockEntityType.Builder.create(N64BlockEntity::new, n64_blocks).build(null));
	}
}
