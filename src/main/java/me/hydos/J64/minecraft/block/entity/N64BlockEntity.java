package me.hydos.J64.minecraft.block.entity;

import me.hydos.J64.minecraft.registry.BlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Tickable;

public class N64BlockEntity extends BlockEntity implements Tickable {

	private boolean broken;
	
	public N64BlockEntity() {
		super(BlockRegistry.N64_BE_TYPE);
	}

	@Override
	public void tick() {
		if (this.world.getBlockState(this.getPos()).get(Properties.WATERLOGGED))
			broken = true;
	}
	
	@Override
	public void fromTag(BlockState state, CompoundTag tag) {
		broken = tag.getBoolean("broken");
	}
	
	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag.putBoolean("broken", broken);
		return tag;
	}
}
