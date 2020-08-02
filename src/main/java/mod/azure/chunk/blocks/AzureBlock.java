package mod.azure.chunk.blocks;

import mod.azure.chunk.util.ModEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AzureBlock extends Block {

	public AzureBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean isMoving) {
		world.getCapability(ModEvents.CAPABILITY, null).ifPresent(cap -> cap.add(pos));
	}

	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		world.getCapability(ModEvents.CAPABILITY, null).ifPresent(cap -> cap.remove(pos));
	}
}