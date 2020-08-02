package mod.azure.chunk.util;

import net.minecraft.util.math.BlockPos;

public interface LoaderInterface {
	void add(BlockPos pos);
    void remove(BlockPos pos);
    boolean contains(BlockPos pos);
}