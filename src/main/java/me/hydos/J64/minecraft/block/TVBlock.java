package me.hydos.J64.minecraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.Direction;

public class TVBlock extends FacingBlock
{
	public TVBlock(Settings settings)
	{
		super(settings);
		this.setDefaultState(((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH));
	}

	public BlockState getPlacementState(ItemPlacementContext ctx) {
	   return (BlockState)this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
	}
	
	public BlockState rotate(BlockState state, BlockRotation rotation) {
	   return (BlockState)state.with(FACING, rotation.rotate((Direction)state.get(FACING)));
	}
	
	public BlockState mirror(BlockState state, BlockMirror mirror) {
	   return state.rotate(mirror.getRotation((Direction)state.get(FACING)));
	}
	
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
	   builder.add(FACING);
	}
}
